const exrpess =require('express');
const v1routes = require('./v1');
const router =exrpess.Router();
router.use('/v1',v1routes);
module.exports=router;