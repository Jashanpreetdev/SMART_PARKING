const mongoose = require('mongoose');
const { User } = require('./user');
// const carData = require('./car_data.json');
const carSchema = new mongoose.Schema({
  user: {
    type: mongoose.Schema.Types.ObjectId,  // Corrected type
    ref: "User",  // Reference to the User model
    required: true
},
  companyName: {
    type: String,
    required: true,
  },
  type:{
    type: String,
    required: true,
  },
  model:{
    type: String,
    required: true,
  },
  vehicleNumber :{
    type: String,
    required: true,
  }
  
});

 
module.exports=mongoose.model('Car', carSchema);