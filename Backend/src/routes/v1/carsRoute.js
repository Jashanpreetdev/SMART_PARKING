const express = require('express');
const router = express.Router();
const {CarController} = require('../../controllers');
const { validateAuthRequest } = require('../../middleware');

router.get('/get?', CarController.getAllCars);
router.post('/add',validateAuthRequest.checkAuth, CarController.addCar);



module.exports = router;