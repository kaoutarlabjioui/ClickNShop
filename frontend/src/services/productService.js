import api from './api';




const productService = {


    getAll: async(page = 0,size = 10, sortBy = 'name') =>{
        const response = await api.get('/products',{
            params: {page,size, sortBy}
        });
        return response.data;
    },

    getById: async(id)=>{
        const response = await api.get(`/products/${id}`);
        return response.data;
    },

    searchByName: async(name)=> {
        const response = await api.get('/products/search/name', {
            params: {name}
        });
        return response.data;
    },

    create: async (productData)=>{
        const response = await api.post('/products', productData);
        return response.data;
    },

    update: async(id,productData)=>{
        const response = await api.put(`/products/${id}`,productData);
        return response.data;
    },

    delete: async(id)=>{
        const response= await api.delete(`/products/${id}`)
    }

};

export default productService;
