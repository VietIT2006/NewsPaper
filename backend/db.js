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
      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
  `;

  await pool.query(createUsersTableQuery);
  console.log(`Kết nối database '${dbName}' thành công và kiểm tra bảng users.`);
}

function getPool() {
  return pool;
}

module.exports = { initDb, getPool };
