const mongoose=require('mongoose');
const { data } = require('../utils/common/success-response');


const bookingSchema= new mongoose.Schema({
    user:{
        type:mongoose.Schema.Types.ObjectId,
        ref:'User',
        require:true
    },
    parking:{
        type:mongoose.Schema.Types.ObjectId,
        ref:'Parking',
        require:true
    },
    car:{
        type:mongoose.Schema.Types.ObjectId,
        ref:'Car',
        require:true
    },
    duration:{
        start:{
            type:String,
            require:true
        },
        end:{
            type:String,
            require:true
        }
    },
    dateOfBooking:{
        type:Date,
    }
},{
    timestamps:true
});



module.exports=mongoose.model('Booking',bookingSchema);