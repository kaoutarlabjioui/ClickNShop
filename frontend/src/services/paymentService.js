import api from './api';

const paymentService = {

    addPayment: async (orderId, paymentData) => {
        const response = await api.post(`/${orderId}/payments`, paymentData);
        return response.data;
    }
};

export default paymentService;