import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { productApi, queueApi } from '../api';

/**
 * 상품 상세 페이지
 *
 * [시연 시나리오]
 * 상품 정보 확인 -> 구매하기 버튼 -> 대기열 페이지로 이동
 */
function ProductDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [purchasing, setPurchasing] = useState(false);

  useEffect(() => {
    const fetchProduct = async () => {
      try {
        const data = await productApi.getById(id);
        setProduct(data);
      } catch (error) {
        console.error('상품 조회 실패:', error);
        alert('상품을 찾을 수 없습니다.');
        navigate('/');
      } finally {
        setLoading(false);
      }
    };

    fetchProduct();
  }, [id, navigate]);

  const formatPrice = (price) => {
    return new Intl.NumberFormat('ko-KR').format(price);
  };

  const handlePurchase = async () => {
    setPurchasing(true);
    try {
      // 대기열 진입
      const entry = await queueApi.enter(product.id);
      // 토큰 저장
      sessionStorage.setItem(`queue_token_${product.id}`, entry.token);
      // 대기열 페이지로 이동
      navigate(`/queue/${product.id}`);
    } catch (error) {
      console.error('대기열 진입 실패:', error);
      alert(error.message || '대기열 진입에 실패했습니다.');
      setPurchasing(false);
    }
  };

  if (loading) {
    return <div className="spinner" />;
  }

  if (!product) {
    return <div>상품을 찾을 수 없습니다.</div>;
  }

  return (
    <div>
      <header className="header">
        <Link to="/" style={{ textDecoration: 'none', color: 'white' }}>
          <h1>🫒 올리브영</h1>
        </Link>
      </header>

      <div className="product-detail">
        <img
          src={product.imageUrl}
          alt={product.name}
          className="product-detail-image"
        />

        <div className="product-category" style={{ marginBottom: '0.5rem' }}>
          {product.category}
        </div>

        <h2 className="product-detail-name">{product.name}</h2>

        <p className="product-detail-description">{product.description}</p>

        <div className="product-detail-price">
          {product.isSaleActive && product.discountRate > 0 ? (
            <>
              <span
                style={{
                  textDecoration: 'line-through',
                  color: '#999',
                  fontSize: '1.25rem',
                  marginRight: '1rem',
                }}
              >
                ₩{formatPrice(product.originalPrice)}
              </span>
              <span style={{ color: '#e63946' }}>
                ₩{formatPrice(product.discountedPrice)}
              </span>
              <span
                className="discount-badge"
                style={{ marginLeft: '0.5rem', fontSize: '1rem' }}
              >
                {product.discountRate}% OFF
              </span>
            </>
          ) : (
            <span>₩{formatPrice(product.originalPrice)}</span>
          )}
        </div>

        <div style={{ marginTop: '1rem', color: '#666' }}>
          재고: {product.stock}개
        </div>

        <div style={{ marginTop: '2rem', display: 'flex', gap: '1rem' }}>
          <button
            className="btn btn-sale btn-lg"
            style={{ flex: 1 }}
            onClick={handlePurchase}
            disabled={purchasing || product.stock === 0}
          >
            {purchasing ? '처리 중...' : product.stock === 0 ? '품절' : '🛒 구매하기'}
          </button>
          <Link to="/">
            <button className="btn btn-secondary btn-lg">목록으로</button>
          </Link>
        </div>
      </div>
    </div>
  );
}

export default ProductDetailPage;
