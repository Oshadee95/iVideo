package com.example.redhw.videomanagementapp.models;

/*
DataSnapshot -> A DataSnapshot instance contains data from a Firebase Database location. Any time you read Database data, you receive the data as a DataSnapshot.

Once the user model is called inside a DataSnapshot, it will automatically assign retained values accordingly in the user model.
It is important to note that the user model properties must be named exactly the same as the keys in the Firebase Database.

You can simply use the variable by creating an User object and calling its properties
eg: User user = new User();
    System.out.println(user.Address);
*/


public class User {

    public String Address;
    public String Contact;
    public String Email;
    public String Type;
    public String Username;

    public User(){};

}
