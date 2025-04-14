const mongoose= require('mongoose');


const parkingSchema=new mongoose.Schema({
    user:{
        type: mongoose.Schema.Types.ObjectId,
        ref:'User',
        required:true
    },
    street:{
        type:String,
        required:true
    },
    city:{
        type:String,
        required:true
    },
    pincode:{
        type:String,
        required:true
    },
    coordinates:{
        longitude:{
            type:String,
            require:true
        },
        latitude:{
            type:String,
            require:true
        },
    },
    dimensions: {
        length_mm: {
          type: Number,
          required: true
        },
        width_mm: {
          type: Number,
          required: true
        },
        height_mm: {
          type: Number
        }
    },
    startDate:{
        type:String,
        required:true
    },
    endDate:{
        type:String,
        required:true
    },
    startTime:{
        type:String,
        required:true
    },
    endTime:{
        type:String,
        required:true
    }
},{
    timestamps:true
});
module.exports=mongoose.model("Parking",parkingSchema);