const express=require('express');

const { ParkingController } = require('../../controllers');
const { validateAuthRequest } = require('../../middleware');

const router=express.Router();

router.post('/add',validateAuthRequest.checkAuth,ParkingController.addParking);
router.post('/get',ParkingController.getParking);


module.exports=router;