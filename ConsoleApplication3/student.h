#pragma once
#include "degree.h"
#include <string>
#include <iostream>
using namespace std;

/*** Student class definition ***/
class Student
{
public:
	const static int tableSize = 3;

private: //needs to be privat public or protected??~
	string studentID;
	string firstName;
	string lastName;
	string emailAddress;
	int age;
	int* arrDaysToComplete;
	DegreeProgram dprog;

public:
	//default constructor
	Student();

	//full constructor
	Student(
		string studentID,
		string firstName,
		string lastName,
		string emailAddress,
		int age,
		int arrDaysToComplete[],
		DegreeProgram dprog
	);

	//accessors for each instance
	string getStudentID();
	string getFirstName();
	string getLastName();
	string getEmailAddress();
	int getAge();
	int* getDaysToComplete();
	DegreeProgram getDegreeProgram();

	//mutators
	void setID(string studentID);
	void setFirst(string firstName);
	void setLast(string lastName);
	void setEmail(string emailAddress);
	void setAge(int age);
	void setDaysToComplete(int arrDaysToComplete[]);
	void setDegreeProgram(DegreeProgram dprog);
	void print();

	//desturctor
	~Student();

};