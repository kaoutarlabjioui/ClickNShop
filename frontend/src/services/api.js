import axios from 'axios';


const API_BASE_URL = 'http://localhost:8081/api';

const api = axios.create({
    baseURL:API_BASE_URL,
    headers:{
        'Content-Type':'application/json',
    },
});

 api.interceptors.request.use(
     (config)=>{
         const token = localStorage.getItem('token');
         const sessionId = localStorage.getItem('sessionId');
         if(sessionId){
             config.headers['Session-Id']=sessionId;
         }
         if(token){
             config.headers.Authorization = `Bearer ${token}`;
         }
        return config ;
     },
     (error)=>Promise.reject(error)
);


 api.interceptors.response.use(
     (response)=>response,
     (error)=>{
         const{response} = error ;

         if(response?.status === 401){
             localStorage.removeItem('token');
             localStorage.removeItem('sessionId');
             window.location.href = '/login';
         }

         if (response?.status === 403) {
             console.error('Accès interdit');
         }

         if (response?.status === 404) {
             console.error('Ressource introuvable');
         }

         // 422 Unprocessable Entity (erreurs métier)
         if (response?.status === 422) {
             console.error('Erreur métier:', response.data);
         }

         return Promise.reject(error);
     }


 );

 export default api;