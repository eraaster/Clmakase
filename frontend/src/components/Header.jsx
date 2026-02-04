import { Link } from 'react-router-dom';

function Header({ isSaleActive, onStartSale, onEndSale }) {
  return (
    <header className="header">
      <Link to="/" style={{ textDecoration: 'none', color: 'white' }}>
        <h1>🫒 올리브영</h1>
      </Link>
      <div className="header-actions">
        {!isSaleActive ? (
          <button className="btn btn-sale" onClick={onStartSale}>
            🎉 할인시작
          </button>
        ) : (
          <button className="btn btn-secondary" onClick={onEndSale}>
            세일종료
          </button>
        )}
      </div>
    </header>
  );
}

export default Header;
