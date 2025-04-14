const express= require('express');
const { model } = require('mongoose');
const router=express.Router();
const {User} = require('../../models');
const { validateAuthRequest } = require('../../middleware');

router.get('/',validateAuthRequest.checkAuth, async (req,res)=>{
    try {
        const user = req.user;
        // const response = await User.findById(user.userId);
        if (!user) {
            return res.status(404).json({ error: 'User not found' });
        }
        return res.send(`hello ${user.name}`);
    } catch (error) {
        console.error('Error fetching user:', error);
        return res.status(500).json({ error: 'Internal server error' });
    }
})
module.exports =router;