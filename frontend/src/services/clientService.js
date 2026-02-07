  import api from './api';

const clientService={


    getAll: async(page = 0, size = 10 , sortBy= 'name')=>{
        const response = await api.get('/clients',{
            params:{page,size,sortBy}
        });
        return response.data;
    },


    getById : async(id)=>{
        const response = await api.get(`/clients/${id}`);
        return response.data;
    },


    getMyProfile: async()=>{
        const response = await api.get('clients/client-profile');
        return response.data;
    },

    getMyOrders: async(page = 0, size=10)=>{
        const response = await api.get('clients/me/orders',{
            params:{page,size}
        });
        return response.data;
    },

    update: async(id,clientData)=>{
        const response = await api.put(`/admin/clients/${id}`,clientData);
        return response.data;
    },

    delete: async(id)=>{
        const response= await api.delete(`/admin/clients/${id}`);
    }

};

export default clientService;


