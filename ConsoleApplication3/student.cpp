#include "student.h"
#include <string>
#include <iostream>
using namespace std;

/*** Student class functions ***/
Student::Student() //Empty constructor
{
	this->studentID = "";
	this->firstName = "";
	this->lastName = "";
	this->emailAddress = "";
	this->age = 0;
	this->arrDaysToComplete = new int[tableSize];
	for (int i = 0; i < tableSize; i++) this->arrDaysToComplete[i] = 0;
	this->dprog = SOFTWARE;

}

Student::Student(string studentID, string firstName, string lastName, string emailAddress, int age, int arrDaysToComplete[], DegreeProgram dprog)
{
	this->studentID = studentID;
	this->firstName = firstName;
	this->lastName = lastName;
	this->emailAddress = emailAddress;
	this->age = age;
	this->arrDaysToComplete = new int[tableSize];
	for (int i = 0; i < tableSize; i++) this->arrDaysToComplete[i] = arrDaysToComplete[i];
	this->dprog = dprog;
}

//Accessor functions
string Student::getStudentID()
{
	return studentID;
}

string Student::getFirstName()
{
	return firstName;
}

string Student::getLastName()
{
	return lastName;
}

string Student::getEmailAddress()
{
	return emailAddress;
}

int Student::getAge()
{
	return age;
}

int* Student::getDaysToComplete()
{
	return arrDaysToComplete; //pointer to an array
}

DegreeProgram Student::getDegreeProgram()
{
	return dprog;
}


//Mutator functions

void Student::setID(string studentID)
{
	this->studentID = studentID;
}

void Student::setFirst(string firstName)
{
	this->firstName = firstName;
}

void Student::setLast(string lastName)
{
	this->lastName = lastName;
}

void Student::setEmail(string emailAddress)
{
	this->emailAddress = emailAddress;
}

void Student::setAge(int age)
{
	this->age = age;
}

void Student::setDaysToComplete(int arrDaysToComplete[])
{
	for (int i = 0; i < tableSize; i++)
	{
		this->arrDaysToComplete[i] = arrDaysToComplete[i];
	}
}

void Student::setDegreeProgram(DegreeProgram dprog)
{
	this->dprog = dprog;
}

void Student::print()
{
	cout << getStudentID() << '\t' << getFirstName() << '\t' << getLastName() << '\t' << getAge() << '\t';
	int* arrDaysToComplete = getDaysToComplete();
	cout << '\t' << "daysInCourse: " << "{" << arrDaysToComplete[0] << ", " << arrDaysToComplete[1] << ", " << arrDaysToComplete[2] << "}" << '\t';
	cout << "Degree Program: " << DegreeProgramStrings[getDegreeProgram()] << endl;
}

//Destructor
Student::~Student()
{
	if (arrDaysToComplete != nullptr)
	{
		delete[] arrDaysToComplete;
		arrDaysToComplete = nullptr;
	}
}