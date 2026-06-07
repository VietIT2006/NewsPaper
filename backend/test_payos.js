const { PayOS } = require('@payos/node');
require('dotenv').config();

const payOS = new PayOS(
  process.env.PAYOS_CLIENT_ID || 'dummy',
  process.env.PAYOS_API_KEY || 'dummy',
  process.env.PAYOS_CHECKSUM_KEY || 'dummy'
);

async function test() {
  try {
    const orderCode = Number(String(Date.now()).slice(-6) + String(Math.floor(Math.random() * 1000)));
    const body = {
      orderCode: orderCode,
      amount: 50000,
      description: 'NewsApp VIP',
      returnUrl: `http://10.0.2.2:3000/api/payment/payos-return?userId=1&orderCode=${orderCode}`,
      cancelUrl: `http://10.0.2.2:3000/api/payment/payos-cancel`
    };
    console.log("Payload:", body);
    const paymentLinkRes = await payOS.createPaymentLink(body);
    console.log("Success:", paymentLinkRes.checkoutUrl);
  } catch (error) {
    console.error("PayOS Error:", error.response ? error.response.data : error);
  }
}
test();
