import { useNavigate } from 'react-router-dom';

function ProductCard({ product }) {
  const navigate = useNavigate();

  const formatPrice = (price) => {
    return new Intl.NumberFormat('ko-KR').format(price);
  };

  const handleClick = () => {
    navigate(`/product/${product.id}`);
  };

  return (
    <div className="product-card" onClick={handleClick}>
      <img
        src={product.imageUrl}
        alt={product.name}
        className="product-image"
      />
      <div className="product-info">
        <div className="product-category">{product.category}</div>
        <div className="product-name">{product.name}</div>
        <div className="product-price">
          {product.isSaleActive && product.discountRate > 0 ? (
            <>
              <span className="original-price">
                ₩{formatPrice(product.originalPrice)}
              </span>
              <span className="sale-price">
                ₩{formatPrice(product.discountedPrice)}
              </span>
              <span className="discount-badge">{product.discountRate}% OFF</span>
            </>
          ) : (
            <span className="sale-price">
              ₩{formatPrice(product.originalPrice)}
            </span>
          )}
        </div>
      </div>
    </div>
  );
}

export default ProductCard;
