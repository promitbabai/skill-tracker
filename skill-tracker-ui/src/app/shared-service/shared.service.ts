import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SharedService {

   //To transfer data between different Angular components (Login ==> User) we are using Behaviour Subject
  loggedInToken$: Observable<any>;
  private loggedInAssociateToken = new BehaviorSubject<any>(1);

  options  = {
    headers: new HttpHeaders({
      'Accept': 'text/html, application/xhtml+xml, */*',
      'Content-Type': 'application/json; charset=utf-8'
    }),
    responseType: 'text' as 'text'
  };

  
  constructor(private http: HttpClient) {
    this.loggedInToken$ = this.loggedInAssociateToken.asObservable();
   }


  //  Behaviour Subject (Login ==> User). This is a method invoked by the Angular Framework
  //  when the Behaviour subject variable is populated inside in the sender LOGIN component
   populateLoggedInAssociateToken(authRequestData: any){
    this.loggedInAssociateToken.next(authRequestData);
   }

  registerUserDetails(registerRequestModel: any): Observable<any>{
    // console.log('Shared service printing input')
    // console.log(registerRequestModel);
    // return registerRequestModel;
    return this.http.post("http://localhost:8091/skill-tracker/api/v1/engineer/add-profile",registerRequestModel, this.options);
  }
  
  getAllSkills(): Observable<any>{
    return this.http.get("http://localhost:8091/skill-tracker/api/v1/engineer/getAllSkills");
  }

  authenticateUserCredentials(authRequest: any): Observable<any>{
    console.log("Login Service -  validateUserCredentials");
    console.log('Invoking the Auth-Server URL, INPARAMS are shown below');
    console.log(authRequest);
    return this.http.post("http://localhost:8097/authenticate",authRequest, this.options);
  }


  getUserDetailsWithToken(authRequest : any){

 
    let tokenStr = 'Bearer ' + authRequest.token;

    // const headers = new HttpHeaders().set('Authorization', tokenStr);
    const params = new HttpParams()
    .set('associateID', authRequest.username)
    .set('associatePassword', authRequest.password) 

    // const httpOptions = {
    //   headers: new HttpHeaders({      
    //     'Authorization': tokenStr
    //   })
    //   ,
    //   params: new HttpParams()
    //   .set('associateID', authRequest.username)
    //   .set('associatePassword', authRequest.password) 
    //   }

    console.log('Shared service - getUserDetailsWithToken');  
    
    return this.http.get("http://localhost:8091/skill-tracker/api/v1/engineer/getUserDetails",{params});
    
    // return this.http.get("http://localhost:8091/skill-tracker/api/v1/engineer/getUserDetails",{headers, params});
    // return this.http.get("http://localhost:8091/skill-tracker/api/v1/engineer/getUserDetails", httpOptions);

  }

  getAssociateSkillRatings(associateID: any){

    console.log('Shared service printing input')
    console.log(associateID );
    const params = new HttpParams()
    .set('associateID',associateID)
    return this.http.get("http://localhost:8091/skill-tracker/api/v1/engineer/getAssociateSkillRatings",{params});

  }

  updateAssociateSkills(updateProfileRequestModel: any){

    console.log('Shared service - updateAssociateSkills - (CHANGED FROM GET TO POST printing input')
    console.log(updateProfileRequestModel );
    return this.http.post("http://localhost:8091/skill-tracker/api/v1/engineer/update-profile",updateProfileRequestModel, this.options);

  }

  searchWithName(obtainedName: any){
    const params = new HttpParams()
    .set('name',obtainedName)
    return this.http.get("http://localhost:8090/skill-tracker/api/v1/admin/getAssociatesByName",{params});
  }

  searchWithAssociateId(obtainedAssociateId: any){
    const params = new HttpParams()
    .set('associateID',obtainedAssociateId)
    return this.http.get("http://localhost:8090/skill-tracker/api/v1/admin/getAssociateByID",{params});
  }

  searchSkillTopic(obtainedTopic: any){
    const params = new HttpParams()
    .set('topic',obtainedTopic)
    return this.http.get("http://localhost:8090/skill-tracker/api/v1/admin/getAssociatesBySkill",{params});
  }


  // http://localhost:8070/skill-tracker/api/v1/admin/getAssociatesBySkill?topic=angular
  // http://localhost:8070/skill-tracker/api/v1/admin/getAssociatesByName?name=Gangotri%20Basu
  // http://localhost:8070/skill-tracker/api/v1/admin/getAssociatesByName?name=TS
  // http://localhost:8070/skill-tracker/api/v1/admin/getAllAssociates
  // http://localhost:8090/skill-tracker/api/v1/admin/getAssociateByID?associateID=174567
  // http://localhost:8090/skill-tracker/api/v1/admin/getAssociateByID?associateID=63bd7b306cbf4c84b4009882

  

}
