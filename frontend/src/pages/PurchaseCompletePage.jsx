import { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';

/**
 * 구매 완료 페이지
 *
 * [시연 시나리오]
 * 구매 완료 메시지 표시 -> 5초 후 자동으로 메인 페이지 이동
 */
function PurchaseCompletePage() {
  const navigate = useNavigate();
  const [countdown, setCountdown] = useState(5);

  const purchaseData = JSON.parse(
    sessionStorage.getItem('lastPurchase') || '{}'
  );

  useEffect(() => {
    const timer = setInterval(() => {
      setCountdown((prev) => {
        if (prev <= 1) {
          clearInterval(timer);
          navigate('/');
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, [navigate]);

  const formatPrice = (price) => {
    return new Intl.NumberFormat('ko-KR').format(price);
  };

  return (
    <div>
      <header className="header">
        <Link to="/" style={{ textDecoration: 'none', color: 'white' }}>
          <h1>🫒 올리브영</h1>
        </Link>
      </header>

      <div className="purchase-complete">
        <div className="success-icon">✅</div>
        <div className="success-message">구매가 완료되었습니다!</div>

        {purchaseData.productName && (
          <div style={{ marginTop: '1.5rem', textAlign: 'left' }}>
            <div
              style={{
                padding: '1rem',
                background: '#f5f5f7',
                borderRadius: '8px',
              }}
            >
              <p>
                <strong>상품:</strong> {purchaseData.productName}
              </p>
              <p>
                <strong>수량:</strong> {purchaseData.quantity}개
              </p>
              <p>
                <strong>결제 금액:</strong> ₩{formatPrice(purchaseData.totalPrice)}
              </p>
              <p>
                <strong>주문 번호:</strong> #{purchaseData.orderId}
              </p>
            </div>
          </div>
        )}

        <p style={{ marginTop: '2rem', color: '#666' }}>
          {countdown}초 후 메인 화면으로 이동합니다.
        </p>

        <Link to="/">
          <button
            className="btn btn-primary btn-lg"
            style={{ marginTop: '1rem' }}
          >
            메인으로 돌아가기
          </button>
        </Link>
      </div>
    </div>
  );
}

export default PurchaseCompletePage;
