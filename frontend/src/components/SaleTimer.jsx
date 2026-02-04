import { useState, useEffect } from 'react';

/**
 * 세일 카운트다운 타이머
 *
 * [시연 포인트]
 * 30초 카운트다운이 끝나면 자동으로 세일 시작
 * 또는 '할인시작' 버튼으로 즉시 시작 가능
 */
function SaleTimer({ onTimerEnd, isRunning }) {
  const [seconds, setSeconds] = useState(30);

  useEffect(() => {
    if (!isRunning) {
      setSeconds(30);
      return;
    }

    if (seconds <= 0) {
      onTimerEnd();
      return;
    }

    const timer = setInterval(() => {
      setSeconds((prev) => prev - 1);
    }, 1000);

    return () => clearInterval(timer);
  }, [seconds, isRunning, onTimerEnd]);

  if (!isRunning) return null;

  return (
    <div className="timer-container">
      <div className="timer-label">🔥 특가 세일까지</div>
      <div className="timer-display">{seconds}</div>
      <div className="timer-label">초</div>
    </div>
  );
}

export default SaleTimer;
