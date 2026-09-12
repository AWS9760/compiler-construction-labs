#include <iostream>
using namespace std;

int x = 10;

int main(){
    cout<<"Global x = "<< x <<endl;
    {
        int x = 80;
        cout<<"Inner x = "<< x <<endl;
        {
            int x = 40;
            cout<<"Innermost x = "<< x <<endl;
        }
        cout<<"Inner x = "<< x <<endl;
    }
    cout<<"Global x = "<< x <<endl;
    return 0;
}