const express = require('express');
const cors = require('cors');
const bcrypt = require('bcryptjs');
const { initDb, getPool } = require('./db');
require('dotenv').config();

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
      user: { id: result.insertId, username, role: userRole }
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
        role: user.role
      }
    });

  } catch (error) {
    console.error(error);
    res.status(500).json({ success: false, message: 'Lỗi server.' });
  }
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`Server Backend đang chạy tại http://localhost:${PORT}`);
});
