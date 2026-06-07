const express = require('express');
const cors = require('cors');
const bcrypt = require('bcryptjs');
const { initDb, getPool } = require('./db');
const { PayOS } = require('@payos/node');
require('dotenv').config();

const payOS = new PayOS({
  clientId: process.env.PAYOS_CLIENT_ID || 'dummy',
  apiKey: process.env.PAYOS_API_KEY || 'dummy',
  checksumKey: process.env.PAYOS_CHECKSUM_KEY || 'dummy'
});

const app = express();
app.use(cors());
app.use(express.json());

// Khởi tạo Database
initDb().catch(err => {
  console.error("Lỗi khởi tạo Database:", err);
  process.exit(1);
});

// ==========================
// API ĐĂNG KÝ
// ==========================
app.post('/api/auth/register', async (req, res) => {
  try {
    const { username, password, role } = req.body;
    
    if (!username || !password) {
      return res.status(400).json({ success: false, message: 'Vui lòng nhập đầy đủ thông tin.' });
    }

    const pool = getPool();
    
    // Kiểm tra username đã tồn tại chưa
    const [rows] = await pool.query('SELECT id FROM users WHERE username = ?', [username]);
    if (rows.length > 0) {
      return res.status(400).json({ success: false, message: 'Tên đăng nhập đã tồn tại.' });
    }

    // Mã hoá mật khẩu
    const salt = await bcrypt.genSalt(10);
    const hashedPassword = await bcrypt.hash(password, salt);

    // Lưu user mới
    const userRole = (role === 'admin') ? 'admin' : 'user';
    const [result] = await pool.query('INSERT INTO users (username, password, role) VALUES (?, ?, ?)', [username, hashedPassword, userRole]);

    res.status(201).json({ 
      success: true, 
      message: 'Đăng ký thành công.',
      user: { id: result.insertId, username, role: userRole, is_premium: false, free_uses_left: 3 }
    });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Lỗi server.' });
  }
});

// ==========================
// API ĐĂNG NHẬP
// ==========================
app.post('/api/auth/login', async (req, res) => {
  try {
    const { username, password } = req.body;

    if (!username || !password) {
      return res.status(400).json({ success: false, message: 'Vui lòng nhập đầy đủ thông tin.' });
    }

    const pool = getPool();
    const [rows] = await pool.query('SELECT * FROM users WHERE username = ?', [username]);
    
    if (rows.length === 0) {
      return res.status(400).json({ success: false, message: 'Tài khoản không tồn tại.' });
    }

    const user = rows[0];

    // So sánh mật khẩu
    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) {
      return res.status(400).json({ success: false, message: 'Sai mật khẩu.' });
    }

    res.status(200).json({
      success: true,
      message: 'Đăng nhập thành công.',
      user: {
        id: user.id,
        username: user.username,
        role: user.role,
        is_premium: !!user.is_premium,
        free_uses_left: user.free_uses_left
      }
    });

  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Lỗi server.' });
  }
});

// ==========================
// API GET PROFILE (Có tự động kiểm tra giao dịch PayOS bị treo)
// ==========================
app.get('/api/user/profile/:id', async (req, res) => {
  try {
    const userId = req.params.id;
    const pool = getPool();
    const [rows] = await pool.query('SELECT id, username, role, is_premium, free_uses_left FROM users WHERE id = ?', [userId]);
    
    if (rows.length === 0) return res.status(404).json({ success: false, message: 'User not found' });
    const user = rows[0];

    // NẾU CHƯA PREMIUM, THỬ KIỂM TRA LẠI CÁC ĐƠN HÀNG PAYOS PENDING
    if (!user.is_premium) {
      const [pendingTx] = await pool.query('SELECT description FROM transactions WHERE user_id = ? AND description LIKE "PENDING_PAYOS_%"', [userId]);
      for (const tx of pendingTx) {
        const orderCode = tx.description.replace('PENDING_PAYOS_', '');
        try {
          const paymentLinkInfo = await payOS.paymentRequests.get(String(orderCode));
          if (paymentLinkInfo && paymentLinkInfo.status === 'PAID') {
            await pool.query('UPDATE users SET is_premium = 1 WHERE id = ?', [userId]);
            await pool.query('UPDATE transactions SET description = ? WHERE description = ?', [`PayOS ${orderCode}`, tx.description]);
            user.is_premium = 1; // Cập nhật ngay kết quả trả về
            break; // Đã là VIP rồi thì không cần check các mã khác
          }
        } catch(e) {
          // Lỗi lấy thông tin đơn hàng này, bỏ qua
        }
      }
    }

    user.is_premium = !!user.is_premium;
    res.json({ success: true, user: user });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// ==========================
// API USE FEATURE (Trừ lượt)
// ==========================
app.post('/api/user/use-feature', async (req, res) => {
  try {
    const { userId } = req.body;
    const pool = getPool();
    const [rows] = await pool.query('SELECT is_premium, free_uses_left FROM users WHERE id = ?', [userId]);
    if (rows.length === 0) return res.status(404).json({ success: false, message: 'User not found' });
    
    const user = rows[0];
    if (user.is_premium) {
      return res.json({ success: true, message: 'Premium user', free_uses_left: user.free_uses_left });
    }
    if (user.free_uses_left <= 0) {
      return res.status(403).json({ success: false, message: 'Hết lượt dùng thử' });
    }
    
    await pool.query('UPDATE users SET free_uses_left = free_uses_left - 1 WHERE id = ?', [userId]);
    res.json({ success: true, message: 'Đã trừ 1 lượt', free_uses_left: user.free_uses_left - 1 });
  } catch (error) {
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// ==========================
// API MUA GÓI PREMIUM (Cũ)
// ==========================
app.post('/api/payment/buy-premium', async (req, res) => {
  try {
    const { userId } = req.body;
    const pool = getPool();
    await pool.query('UPDATE users SET is_premium = 1 WHERE id = ?', [userId]);
    await pool.query('INSERT INTO transactions (user_id, amount, description) VALUES (?, ?, ?)', [userId, 50000, 'Mua gói Premium (Dev)']);
    res.json({ success: true, message: 'Nâng cấp Premium thành công' });
  } catch (error) {
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// ==========================
// API PAYOS CREATE LINK
// ==========================
app.post('/api/payment/payos-create', async (req, res) => {
  try {
    const { userId } = req.body;
    const orderCode = Number(String(Date.now()).slice(-6) + String(Math.floor(Math.random() * 1000)));

    // Lưu một giao dịch PENDING để giữ userId
    const pool = getPool();
    await pool.query('INSERT INTO transactions (user_id, amount, description) VALUES (?, ?, ?)', [userId, 50000, `PENDING_PAYOS_${orderCode}`]);

    const body = {
      orderCode: orderCode,
      amount: 50000,
      description: 'NewsApp VIP',
      // Dùng URL ngrok hoặc URL tuỳ chỉnh nếu dev app mobile. Do webview mở trực tiếp url, ta có thể cho nó quay lại 1 trang HTML thông báo:
      returnUrl: `http://10.0.2.2:3000/api/payment/payos-return?userId=${userId}&orderCode=${orderCode}`,
      cancelUrl: `http://10.0.2.2:3000/api/payment/payos-cancel`
    };

    const paymentLinkRes = await payOS.paymentRequests.create(body);

    res.json({
      success: true,
      checkoutUrl: paymentLinkRes.checkoutUrl,
      orderCode: orderCode
    });
  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// ==========================
// API PAYOS RETURN (Dành cho WebView khi thanh toán xong)
// ==========================
app.get('/api/payment/payos-return', async (req, res) => {
  try {
    const { userId, orderCode, code } = req.query;
    if (code !== '00') {
      return res.send('<h2 style="text-align:center; margin-top:50px;">Thanh toán đã hủy. Vui lòng đóng màn hình này.</h2>');
    }

    const paymentLinkInfo = await payOS.paymentRequests.get(orderCode);
    if (paymentLinkInfo.status === 'PAID') {
      const pool = getPool();
      await pool.query('UPDATE users SET is_premium = 1 WHERE id = ?', [userId]);
      await pool.query('UPDATE transactions SET description = ? WHERE description = ?', [`PayOS ${orderCode}`, `PENDING_PAYOS_${orderCode}`]);
      return res.send('<h2 style="text-align:center; color:green; margin-top:50px;">THANH TOÁN THÀNH CÔNG!<br>Bạn đã trở thành VIP.<br>Hãy ĐÓNG màn hình này để quay lại App.</h2>');
    } else {
      return res.send('<h2 style="text-align:center; margin-top:50px;">Đơn hàng chưa thanh toán.</h2>');
    }
  } catch (error) {
    res.send('<h2>Lỗi xử lý.</h2>');
  }
});

// ==========================
// API PAYOS CANCEL
// ==========================
app.get('/api/payment/payos-cancel', (req, res) => {
  res.send('<h2 style="text-align:center; margin-top:50px;">Giao dịch đã hủy. Vui lòng đóng màn hình này.</h2>');
});

// ==========================
// API PAYOS CHECK TRỰC TIẾP TỪ APP (Bulletproof)
// ==========================
app.post('/api/payment/payos-check', async (req, res) => {
  try {
    const { userId, orderCode } = req.body;
    const paymentLinkInfo = await payOS.paymentRequests.get(String(orderCode));
    
    if (paymentLinkInfo.status === 'PAID') {
      const pool = getPool();
      await pool.query('UPDATE users SET is_premium = 1 WHERE id = ?', [userId]);
      await pool.query('UPDATE transactions SET description = ? WHERE description = ?', [`PayOS ${orderCode}`, `PENDING_PAYOS_${orderCode}`]);
      return res.json({ success: true, is_premium: true });
    }
    res.json({ success: true, is_premium: false });
  } catch (error) {
    console.error(error);
    res.json({ success: false, is_premium: false });
  }
});

// ==========================
// API PAYOS WEBHOOK (Dành cho Localtunnel)
// ==========================
app.post('/api/payment/payos-webhook', async (req, res) => {
  try {
    const webhookData = payOS.webhooks.verifyPaymentWebhookData(req.body);
    const orderCode = webhookData.orderCode;
    
    const pool = getPool();
    // Tìm userId dựa trên transactions PENDING
    const [rows] = await pool.query('SELECT user_id FROM transactions WHERE description = ?', [`PENDING_PAYOS_${orderCode}`]);
    if (rows.length > 0) {
        const userId = rows[0].user_id;
        await pool.query('UPDATE users SET is_premium = 1 WHERE id = ?', [userId]);
        await pool.query('UPDATE transactions SET description = ? WHERE description = ?', [`PayOS ${orderCode}`, `PENDING_PAYOS_${orderCode}`]);
    }
    res.json({ error: 0, message: "Ok", data: webhookData });
  } catch (e) {
    res.json({ error: 1, message: e.message });
  }
});

// ==========================
// API ADMIN REVENUE
// ==========================
app.get('/api/admin/revenue', async (req, res) => {
  try {
    const pool = getPool();
    const dateParam = req.query.date;

    // 1. Tính TỔNG DOANH THU TOÀN BỘ (overall_total)
    const [overallSumResult] = await pool.query('SELECT SUM(amount) as total FROM transactions WHERE description NOT LIKE "PENDING_PAYOS_%"');
    const overallTotal = overallSumResult[0].total || 0;

    let total_revenue = overallTotal;
    let transactions = [];

    if (dateParam) {
      // 2a. Nếu có truyền ngày -> Tính doanh thu và lấy danh sách của ngày đó
      const [dailySumResult] = await pool.query('SELECT SUM(amount) as total FROM transactions WHERE description NOT LIKE "PENDING_PAYOS_%" AND DATE(created_at) = ?', [dateParam]);
      total_revenue = dailySumResult[0].total || 0;

      const [dailyTransactions] = await pool.query('SELECT t.id, t.amount, t.description, t.created_at, u.username FROM transactions t JOIN users u ON t.user_id = u.id WHERE t.description NOT LIKE "PENDING_PAYOS_%" AND DATE(t.created_at) = ? ORDER BY t.created_at DESC', [dateParam]);
      transactions = dailyTransactions;
    } else {
      // 2b. Nếu không truyền ngày -> Lấy toàn bộ danh sách
      const [allTransactions] = await pool.query('SELECT t.id, t.amount, t.description, t.created_at, u.username FROM transactions t JOIN users u ON t.user_id = u.id WHERE t.description NOT LIKE "PENDING_PAYOS_%" ORDER BY t.created_at DESC');
      transactions = allTransactions;
    }
    
    res.json({
      success: true,
      overall_revenue: overallTotal,
      total_revenue: total_revenue,
      transactions: transactions
    });
  } catch (error) {
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// ==========================
// API SOURCE MANAGEMENT (Global RSS)
// ==========================

// Lấy danh sách nguồn tin
app.get('/api/sources', async (req, res) => {
  try {
    const pool = getPool();
    const [sources] = await pool.query('SELECT * FROM rss_sources ORDER BY id ASC');
    
    // Map is_enabled from 1/0 to true/false for Android Gson parsing
    const mappedSources = sources.map(s => ({
      ...s,
      is_enabled: s.is_enabled === 1
    }));

    res.json({ success: true, sources: mappedSources });
  } catch (error) {
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Thêm nguồn tin mới (Admin)
app.post('/api/admin/sources', async (req, res) => {
  try {
    const { name, url } = req.body;
    if (!name || !url) return res.status(400).json({ success: false, message: 'Missing name or url' });
    const pool = getPool();
    await pool.query('INSERT INTO rss_sources (name, url, is_enabled) VALUES (?, ?, ?)', [name, url, 1]);
    res.json({ success: true, message: 'Đã thêm nguồn tin' });
  } catch (error) {
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Cập nhật trạng thái nguồn tin (Admin)
app.put('/api/admin/sources/:id', async (req, res) => {
  try {
    const sourceId = req.params.id;
    const { is_enabled } = req.body;
    const pool = getPool();
    await pool.query('UPDATE rss_sources SET is_enabled = ? WHERE id = ?', [is_enabled ? 1 : 0, sourceId]);
    res.json({ success: true, message: 'Đã cập nhật trạng thái' });
  } catch (error) {
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, '0.0.0.0', () => {
  console.log(`Server Backend đang chạy tại http://0.0.0.0:${PORT}`);
});
