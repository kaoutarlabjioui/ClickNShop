import api from './api'

const authService = {
    login: async(credentials)=>{
        const response =  await api.post('/auth/login',credentials);
        const {token , sessionId}= response.data;
    },

    logout: async()=>{
        const sessionId = localStorage.getItem('sessionId');
        await api.post('/auth/logout',null,{
            headers: {'Session-Id': sessionId}
        });
        localStorage.clear();
    }



    getCurrentUser: async()=>{
        const response = await api.get('/auth/me');
        return response.data;
    },

    registerClient: async (clientData)=>{
        const response = await api.post('/auth/registre-client',clientData);
        return response.data;
    }

};

export default authService;