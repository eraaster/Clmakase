import { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { queueApi, purchaseApi } from '../api';

/**
 * 대기열 페이지
 *
 * [시연 시나리오]
 * 1. 대기 순번 표시: "157명" -> "84명" -> "12명" 실시간 감소
 * 2. canPurchase가 true가 되면 자동으로 구매 처리
 * 3. 구매 완료 시 완료 페이지로 이동
 *
 * [면접 포인트]
 * Q: "실시간 순번 업데이트를 어떻게 처리했나요?"
 * A: 2초 간격 Polling으로 구현했습니다.
 *    WebSocket 대비 트레이드오프:
 *    - 구현 간단, 연결 관리 불필요
 *    - EKS Pod 재시작 시에도 안정적
 *    - 실시간성은 약간 떨어지지만 시연에는 충분
 */
function QueuePage() {
  const { productId } = useParams();
  const navigate = useNavigate();
  const [position, setPosition] = useState(0);
  const [estimatedWait, setEstimatedWait] = useState(0);
  const [canPurchase, setCanPurchase] = useState(false);
  const [purchasing, setPurchasing] = useState(false);
  const [error, setError] = useState(null);

  const token = sessionStorage.getItem(`queue_token_${productId}`);

  const checkQueueStatus = useCallback(async () => {
    if (!token) {
      setError('대기열 정보가 없습니다. 다시 시도해주세요.');
      return;
    }

    try {
      const status = await queueApi.getStatus(productId, token);

      if (status.expired) {
        setError('대기열이 만료되었습니다. 다시 시도해주세요.');
        return;
      }

      setPosition(status.position);
      setEstimatedWait(status.estimatedWaitSeconds);
      setCanPurchase(status.canPurchase);
    } catch (err) {
      console.error('대기열 상태 조회 실패:', err);
    }
  }, [productId, token]);

  // 대기열 상태 폴링
  useEffect(() => {
    if (!token) {
      navigate('/');
      return;
    }

    checkQueueStatus();

    const interval = setInterval(checkQueueStatus, 2000);

    return () => clearInterval(interval);
  }, [token, checkQueueStatus, navigate]);

  // 구매 가능 상태가 되면 자동 구매
  useEffect(() => {
    if (canPurchase && !purchasing) {
      handlePurchase();
    }
  }, [canPurchase]);

  const handlePurchase = async () => {
    setPurchasing(true);
    try {
      const result = await purchaseApi.purchase(productId, 1, token);

      // 토큰 삭제
      sessionStorage.removeItem(`queue_token_${productId}`);

      // 구매 정보 저장
      sessionStorage.setItem('lastPurchase', JSON.stringify(result));

      // 완료 페이지로 이동
      navigate('/complete');
    } catch (err) {
      console.error('구매 실패:', err);
      setError(err.message || '구매 처리 중 오류가 발생했습니다.');
      setPurchasing(false);
    }
  };

  if (error) {
    return (
      <div>
        <header className="header">
          <Link to="/" style={{ textDecoration: 'none', color: 'white' }}>
            <h1>🫒 올리브영</h1>
          </Link>
        </header>
        <div className="queue-container">
          <div className="queue-icon">😢</div>
          <div className="queue-message" style={{ color: '#e63946' }}>
            {error}
          </div>
          <Link to="/">
            <button className="btn btn-primary btn-lg">메인으로 돌아가기</button>
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div>
      <header className="header">
        <Link to="/" style={{ textDecoration: 'none', color: 'white' }}>
          <h1>🫒 올리브영</h1>
        </Link>
      </header>

      <div className="queue-container">
        {purchasing ? (
          <>
            <div className="queue-icon">🎉</div>
            <div className="queue-message">구매 처리 중입니다...</div>
            <div className="spinner" />
          </>
        ) : canPurchase ? (
          <>
            <div className="queue-icon">✨</div>
            <div className="queue-message">구매가 가능합니다!</div>
            <button
              className="btn btn-sale btn-lg"
              onClick={handlePurchase}
              disabled={purchasing}
            >
              지금 구매하기
            </button>
          </>
        ) : (
          <>
            <div className="queue-icon">⏳</div>
            <div className="queue-message">현재 대기 순번</div>
            <div className="queue-position">{position}명</div>
            <div className="queue-message">
              예상 대기 시간: 약 {estimatedWait}초
            </div>
            <div className="queue-progress">
              <div
                className="queue-progress-bar"
                style={{
                  width: `${Math.max(0, 100 - (position / 100) * 100)}%`,
                }}
              />
            </div>
            <p style={{ color: '#999', fontSize: '0.875rem' }}>
              잠시만 기다려 주세요. 순번이 되면 자동으로 구매가 진행됩니다.
            </p>
          </>
        )}
      </div>
    </div>
  );
}

export default QueuePage;
