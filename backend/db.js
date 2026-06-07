const mysql = require('mysql2/promise');
require('dotenv').config();

let pool;

async function initDb() {
  // Kết nối tới server MySQL (chưa chọn database cụ thể)
  const connection = await mysql.createConnection({
    host: process.env.DB_HOST || 'localhost',
    user: process.env.DB_USER || 'root',
    password: process.env.DB_PASS || '',
  });

  const dbName = process.env.DB_NAME || 'newspaper_db';

  // Tạo database nếu chưa tồn tại
  await connection.query(`CREATE DATABASE IF NOT EXISTS \`${dbName}\`;`);
  await connection.end();

  // Tạo Connection Pool tới database vừa tạo
  pool = mysql.createPool({
    host: process.env.DB_HOST || 'localhost',
    user: process.env.DB_USER || 'root',
    password: process.env.DB_PASS || '',
    database: dbName,
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0
  });

  // Tạo bảng users
  const createUsersTableQuery = `
    CREATE TABLE IF NOT EXISTS users (
      id INT AUTO_INCREMENT PRIMARY KEY,
      username VARCHAR(50) NOT NULL UNIQUE,
      password VARCHAR(255) NOT NULL,
      role VARCHAR(20) DEFAULT 'user',
      is_premium BOOLEAN DEFAULT false,
      free_uses_left INT DEFAULT 3,
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
  `;
  await pool.query(createUsersTableQuery);

  // Thử thêm cột nếu bảng users đã tồn tại từ trước
  try {
    await pool.query('ALTER TABLE users ADD COLUMN is_premium BOOLEAN DEFAULT false;');
  } catch(e) {}
  try {
    await pool.query('ALTER TABLE users ADD COLUMN free_uses_left INT DEFAULT 3;');
  } catch(e) {}

  // Tạo bảng transactions
  const createTransactionsTableQuery = `
    CREATE TABLE IF NOT EXISTS transactions (
      id INT AUTO_INCREMENT PRIMARY KEY,
      user_id INT NOT NULL,
      amount INT NOT NULL,
      description VARCHAR(255),
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );
  `;
  await pool.query(createTransactionsTableQuery);

  // Tạo bảng rss_sources
  const createRssSourcesTableQuery = `
    CREATE TABLE IF NOT EXISTS rss_sources (
      id INT AUTO_INCREMENT PRIMARY KEY,
      name VARCHAR(100) NOT NULL,
      url VARCHAR(255) NOT NULL,
      is_enabled BOOLEAN DEFAULT true,
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
  `;
  await pool.query(createRssSourcesTableQuery);

  // Thêm dữ liệu mặc định nếu bảng trống
  const [sourcesCount] = await pool.query('SELECT COUNT(*) as count FROM rss_sources');
  if (sourcesCount[0].count === 0) {
    const defaultSources = [
      ['Thanh Niên', 'https://thanhnien.vn/rss/home.rss', 1],
      ['VnExpress', 'https://vnexpress.net/rss/tin-moi-nhat.rss', 0],
      ['Tuổi Trẻ', 'https://tuoitre.vn/rss/tin-moi-nhat.rss', 0],
      ['Dân Trí', 'https://dantri.com.vn/rss/home.rss', 0],
      ['Zing News', 'https://zingnews.vn/rss/tin-moi.rss', 0]
    ];
    for (let source of defaultSources) {
      await pool.query('INSERT INTO rss_sources (name, url, is_enabled) VALUES (?, ?, ?)', source);
    }
  }

  console.log(`Kết nối database '${dbName}' thành công và khởi tạo các bảng: users, transactions, rss_sources.`);
}

function getPool() {
  return pool;
}

module.exports = { initDb, getPool };
