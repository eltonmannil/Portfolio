#include <iostream>
#include "roster.h"
using namespace std;

Roster::Roster()
{
	this->maxSize = 0;
	this->lastIndex = -1;
	this->classRosterArray = nullptr;
}

Roster::Roster(int maxSize)
{
	this->maxSize = maxSize;
	this->lastIndex = -1;
	this->classRosterArray = new Student * [maxSize];
}

void Roster::parseThenAdd(string row)
{
	int parseArr[Student::tableSize];
	DegreeProgram degreeprogram;

	if (lastIndex < maxSize)
	{
		lastIndex++;
	}

	this->classRosterArray[lastIndex] = new Student();

	//Parse through the student data strong deliminated by comma
	//read student ID
	int long rhs = row.find(",");
	classRosterArray[lastIndex]->setID(row.substr(0, rhs));

	//read firstName
	int long lhs = rhs + 1;
	rhs = row.find(",", lhs);
	classRosterArray[lastIndex]->setFirst(row.substr(lhs, rhs - lhs));

	//read lastName
	lhs = rhs + 1;
	rhs = row.find(",", lhs);
	classRosterArray[lastIndex]->setLast(row.substr(lhs, rhs - lhs));

	//read emailAddress
	lhs = rhs + 1;
	rhs = row.find(",", lhs);
	classRosterArray[lastIndex]->setEmail(row.substr(lhs, rhs - lhs));

	//read age
	lhs = rhs + 1;
	rhs = row.find(",", lhs);
	int x = stoi(row.substr(lhs, rhs - lhs));
	classRosterArray[lastIndex]->setAge(x);

	//read daysInCourse1
	lhs = rhs + 1;
	rhs = row.find(",", lhs);
	parseArr[0] = stoi(row.substr(lhs, rhs - lhs));

	//read daysInCourse2
	lhs = rhs + 1;
	rhs = row.find(",", lhs);
	parseArr[1] = stoi(row.substr(lhs, rhs - lhs));

	//read daysInCourse3
	lhs = rhs + 1;
	rhs = row.find(",", lhs);
	parseArr[2] = stoi(row.substr(lhs, rhs - lhs));

	//set the days left in course
	classRosterArray[lastIndex]->setDaysToComplete(parseArr);

	//read degree program
	lhs = rhs + 1;
	if (row[lhs] == 'S')
	{
		if (row[lhs + 1] == 'E') classRosterArray[lastIndex]->setDegreeProgram(SECURITY);
		else if (row[lhs + 1] == 'O') classRosterArray[lastIndex]->setDegreeProgram(SOFTWARE);
		else
		{
			cerr << "INVALID DEGREE PROGRAM! EXITING NOW!" << endl;
			exit(-1);
		}
	}
	else if (row[lhs] == 'N') classRosterArray[lastIndex]->setDegreeProgram(NETWORK);
	else
	{
		cerr << "INVALID DEGREE PROGRAM! EXITING NOW!" << endl;
		exit(-1);
	}
}

void Roster::add(string studentID, string firstName, string lastName, string emailAddress, int age, int daysInCourse1, int daysInCourse2, int daysInCourse3, DegreeProgram degreeprogam)
{
	int arrDaysToComplete[Student::tableSize];

	arrDaysToComplete[0] = daysInCourse1;
	arrDaysToComplete[1] = daysInCourse2;
	arrDaysToComplete[2] = daysInCourse3;

	classRosterArray[lastIndex] = new Student(studentID, firstName, lastName, emailAddress, age, arrDaysToComplete, degreeprogam);
}

Student* Roster::getStudent(int index)
{
	return classRosterArray[index];
}

void Roster::printAll()
{
	for (int i = 0; i <= this->lastIndex; i++) (this->classRosterArray)[i]->print();
}

bool Roster::remove(string ID)
{
	bool found = false;
	for (int i = 0; i <= lastIndex; i++)
	{
		if (this->classRosterArray[i]->getStudentID() == ID)
		{
			found = true;
			delete this->classRosterArray[i];
			this->classRosterArray[i] = this->classRosterArray[lastIndex];
			lastIndex--;
		}
	}
	if (!found)
	{
		cout << "Student with ID " << ID << " not found." << endl;
	}
	return found;
}

void Roster::printAverageDaysInCourse(string sID)
{
	bool found = false;
	for (int i = 0; i <= lastIndex; i++)
	{
		if (this->classRosterArray[i]->getStudentID() == sID)
		{
			found = true;
			int* ptr = classRosterArray[i]->getDaysToComplete();
			cout << "Average number of days left in class for " << sID << " is " << (ptr[0] + ptr[1] + ptr[2]) / 3 << "\n";
		}
	}
	if (!found) cout << "Student not found!\n";
}

void Roster::printInvalidEmails()
{
	cout << "Displaying invalid email entries:\n";
	for (int i = 0; i < 5; i++)
	{
		string email = classRosterArray[i]->getEmailAddress();
		if ((email.find("@") == string::npos || email.find(".") == string::npos) || (email.find(" ") != string::npos))
			cout << email << endl;
	}
}

void Roster::printByDegreeProgram(DegreeProgram degreeprogram)
{
	cout << "Printing by degree " << DegreeProgramStrings[degreeprogram] << endl;
	for (int i = 0; i <= lastIndex; i++) {
		if (this->classRosterArray[i]->getDegreeProgram() == degreeprogram) this->classRosterArray[i]->print();
	}
}

Roster::~Roster()
{
	for (int i = 0; i < 5; i++)
	{
		delete this->classRosterArray[i];
	}
	delete this;
}