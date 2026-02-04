import { useState, useEffect, useCallback } from 'react';
import Header from '../components/Header';
import SaleTimer from '../components/SaleTimer';
import ProductCard from '../components/ProductCard';
import { productApi, saleApi } from '../api';

/**
 * 메인 페이지 (상품 목록)
 *
 * [시연 시나리오]
 * 1. 처음 접속 시 정가로 상품 표시
 * 2. '할인시작' 버튼 클릭 또는 타이머 종료
 * 3. 화면이 즉시 갱신되어 할인가 표시
 */
function MainPage() {
  const [products, setProducts] = useState([]);
  const [isSaleActive, setIsSaleActive] = useState(false);
  const [isTimerRunning, setIsTimerRunning] = useState(false);
  const [loading, setLoading] = useState(true);

  const fetchProducts = useCallback(async () => {
    try {
      const data = await productApi.getAll();
      setProducts(data);
      // 상품 데이터에서 세일 상태 확인
      if (data.length > 0) {
        setIsSaleActive(data[0].isSaleActive);
      }
    } catch (error) {
      console.error('상품 조회 실패:', error);
    } finally {
      setLoading(false);
    }
  }, []);

  const checkSaleStatus = useCallback(async () => {
    try {
      const status = await saleApi.getStatus();
      setIsSaleActive(status.saleActive);
    } catch (error) {
      console.error('세일 상태 조회 실패:', error);
    }
  }, []);

  useEffect(() => {
    fetchProducts();
    checkSaleStatus();
  }, [fetchProducts, checkSaleStatus]);

  const handleStartSale = async () => {
    try {
      await saleApi.start();
      setIsSaleActive(true);
      setIsTimerRunning(false);
      // 상품 목록 갱신 (할인가 반영)
      await fetchProducts();
    } catch (error) {
      console.error('세일 시작 실패:', error);
      alert('세일 시작에 실패했습니다.');
    }
  };

  const handleEndSale = async () => {
    try {
      await saleApi.end();
      setIsSaleActive(false);
      await fetchProducts();
    } catch (error) {
      console.error('세일 종료 실패:', error);
    }
  };

  const handleTimerEnd = () => {
    handleStartSale();
  };

  const handleStartTimer = () => {
    setIsTimerRunning(true);
  };

  if (loading) {
    return (
      <div>
        <Header isSaleActive={isSaleActive} />
        <div className="spinner" />
      </div>
    );
  }

  return (
    <div>
      <Header
        isSaleActive={isSaleActive}
        onStartSale={handleStartSale}
        onEndSale={handleEndSale}
      />

      {/* 세일 배너 */}
      <div className={`sale-banner ${isSaleActive ? '' : 'inactive'}`}>
        {isSaleActive ? (
          <strong>🎉 특가 세일 진행 중! 최대 40% 할인</strong>
        ) : (
          <strong>곧 특가 세일이 시작됩니다</strong>
        )}
      </div>

      {/* 타이머 (세일 전에만 표시) */}
      {!isSaleActive && !isTimerRunning && (
        <div className="timer-container">
          <div className="timer-label">세일 카운트다운을 시작하려면</div>
          <button className="btn btn-sale btn-lg" onClick={handleStartTimer}>
            ⏱️ 타이머 시작 (30초)
          </button>
        </div>
      )}

      <SaleTimer isRunning={isTimerRunning} onTimerEnd={handleTimerEnd} />

      {/* 상품 목록 */}
      <div className="products-grid">
        {products.map((product) => (
          <ProductCard key={product.id} product={product} />
        ))}
      </div>
    </div>
  );
}

export default MainPage;
