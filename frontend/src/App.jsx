import { Routes, Route } from 'react-router-dom';
import MainPage from './pages/MainPage';
import ProductDetailPage from './pages/ProductDetailPage';
import QueuePage from './pages/QueuePage';
import PurchaseCompletePage from './pages/PurchaseCompletePage';

function App() {
  return (
    <Routes>
      <Route path="/" element={<MainPage />} />
      <Route path="/product/:id" element={<ProductDetailPage />} />
      <Route path="/queue/:productId" element={<QueuePage />} />
      <Route path="/complete" element={<PurchaseCompletePage />} />
    </Routes>
  );
}

export default App;
