const express =require('express');
const router =express.Router();

const HomeRoute =require('./homeRoute');
const AuthRoute =require('./authRoute');
const CarRoute = require('./carsRoute');
const ParkingRoute=require('./parkingRoute');

router.use('/',HomeRoute);
router.use('/auth',AuthRoute);
router.use('/cars',CarRoute);
router.use('/parking',ParkingRoute);


module.exports=router;