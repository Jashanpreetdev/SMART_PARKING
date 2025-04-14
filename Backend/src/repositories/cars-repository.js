const CrudRepositery = require("./crud-repository");
const { Car, User } = require("../models");
const AppError = require("../utils/errors/app-error");
const { StatusCodes } = require("http-status-codes");


class CarRepository extends CrudRepositery {
    constructor() {
        super(Car);
    }

}
module.exports = CarRepository;