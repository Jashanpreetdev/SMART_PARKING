const CrudRepositery = require("./crud-repository");
const { User } = require("../models");

class SignUpRepository extends CrudRepositery {
    constructor() {
        super(User);
    }
    async findByEmail(email) {
        try {
            return await User.findOne({ email });
        } catch (error) {
            throw new AppError(error.message, StatusCodes.INTERNAL_SERVER_ERROR);
        }
    }
    
}

module.exports = SignUpRepository;

