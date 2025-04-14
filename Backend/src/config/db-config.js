require('dotenv').config(); // Load environment variables from .env

module.exports = {
    MONGODB_URI: process.env.MONGODB_URI||"mongodb+srv://himanshu10092004:zPmh3VIlp4tyFRDq@cluster0.mw6uifn.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"
};