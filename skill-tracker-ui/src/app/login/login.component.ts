import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { SharedService } from 'src/app/shared-service/shared.service';
import { LoginModel } from './model/loginModel';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginSuccess: boolean;
  loginForm:FormGroup;
  loginModel: LoginModel;
  loginData: Subscription;
  allSkills: Subscription;
  loginError: boolean;
  loginErrorMessage: '';

  constructor(
    private formBuilder:FormBuilder,
    private http:HttpClient,
    private router:Router,
    private sharedService:SharedService
    ){
      this.loginModel = new LoginModel();
    }
  ngOnInit(): void {

    this.initializeForm();

  }
  initializeForm(){
    this.loginForm=this.formBuilder.group({
      associateID:['',[Validators.required,Validators.pattern("^CTS[0-9]{5,30}$")]],
      associatePassword:['',[Validators.required,Validators.pattern("(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[$@$!%*?&])[A-Za-z\d$@$!%*?&].{7,}")]],
      // associateID:[''],
      // associatePassword:[''],
    });
  }

  goToRegister(){
    this.router.navigateByUrl('/register');
  }
 

  loginButtonAction(){

    this.populateRestApiCallInParams();
    this.validateUserServiceCall();

  }

 /**
   * Method to populate input obtained from the
   * Login form into the Request Model
   */
 populateRestApiCallInParams(){
  this.loginModel = this.loginForm.value;

  // base64 password encryption
  this.loginModel.associatePassword = btoa(this.loginModel.associatePassword);
  console.log("Encrypted Password is " +this.loginModel.associatePassword);
}

/**
 * Method to call Shared Service function that
 * finds the user into the database
 */
validateUserServiceCall(){
  this.loginData = this.sharedService.validateUserCredentialss(this.loginModel.associateID, this.loginModel.associatePassword).subscribe((success: any) => {
    const obtainedProfile = success;
    console.log(obtainedProfile);
    console.log(obtainedProfile.admin);
    this.loginError = false;
    this.loginErrorMessage = '';
    this.loginSuccess = true;
    // console.log('Login Executed');
    // console.log(associateDataObtained);

    //To transfer data between ifferent Angular components (Login ==> User)
    //we are using Behaviour Subject
    this.sharedService.populateLoggedInAssociateData(obtainedProfile);
    if(obtainedProfile.admin === 'N'){
      this.router.navigateByUrl('/user');
    }else{
      // this.router.navigateByUrl('/user');
      this.router.navigateByUrl('/admin');
    }
    
  },
  (httpErrorResponse: HttpErrorResponse) => {
    this.loginError = true;
    console.log('Login Error');
    console.log(httpErrorResponse.error);
    this.loginErrorMessage = httpErrorResponse.error;
    this.loginSuccess = false;
  },
  () => {}
  );
}

}
