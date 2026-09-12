#include<iostream>
using namespace std;

void q(int x[], int y[]){
    x[10] = 2;
    cout<<"x[10] = "<< x[10] <<endl;
    cout<<"y[10] = "<< y[10] <<endl;
}

int main(){
    cout<<"Case 1: q(a, a) (x and y are aliases)"<<endl;
    int a[20] = {0};
    q(a, a);

    cout<<endl;

    cout<<"Case 2: q(a, b) (x and y are aliases)"<<endl;
    int a2[20] = {0};
    int b[20] = {0};
    q(a2, b);

    return 0;
}