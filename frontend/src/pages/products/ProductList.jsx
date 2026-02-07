import { useEffect, useState } from 'react';
import productService from '../../services/productService';

const ProductList = () => {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    useEffect(() => {
        fetchProducts();
    }, [page]);

    const fetchProducts = async () => {
        try {
            setLoading(true);
            const data = await productService.getAll(page, 10, 'name');
            setProducts(data.content); // Spring Page structure
            setTotalPages(data.totalPages);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    if (loading) return <p>Chargement...</p>;
    if (error) return <p>Erreur : {error}</p>;

    return (
        <div>
            <h1>Liste des Produits</h1>
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Nom</th>
                    <th>Prix</th>
                    <th>Stock</th>
                </tr>
                </thead>
                <tbody>
                {products.map((product) => (
                    <tr key={product.id}>
                        <td>{product.id}</td>
                        <td>{product.name}</td>
                        <td>{product.price.toFixed(2)} DH</td>
                        <td>{product.stock}</td>
                    </tr>
                ))}
                </tbody>
            </table>

            {/* Pagination */}
            <div>
                <button disabled={page === 0} onClick={() => setPage(page - 1)}>
                    Précédent
                </button>
                <span> Page {page + 1} / {totalPages} </span>
                <button disabled={page >= totalPages - 1} onClick={() => setPage(page + 1)}>
                    Suivant
                </button>
            </div>
        </div>
    );
};

export default ProductList;