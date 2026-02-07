import api from './api'

const orderService={
    create: async(orderData)=>{
        const response= await api.post('/orders',orderData);
        return response.data;

    },

    getById: async(id)=>{
        const response = await api.get(`/orders/${id}`);
        return response.date;
    },

    getByClient: async(clientId, page = 0 ,size = 10)=>{
        const response = await api.get(`/orders/client/${clientId}`,{
            params:{page,size}
        });
        return response.data;
    },

    confirm: async(id)=>{
        const response = await api.post(`/order/${id}/confirm`);
        return response.data;
    },

    cancel: async (id)=>{
        const response = await api.post(`/arder/${id}/cancel`);
        return response.data;
    }
};

export default orderService;