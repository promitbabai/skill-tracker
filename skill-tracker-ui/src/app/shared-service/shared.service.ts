import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SharedService {

  associateData$: Observable<any>;
  private loggedInAssociateData = new BehaviorSubject<any>(1);

  constructor(
    private http: HttpClient,
  ) {
    this.associateData$ = this.loggedInAssociateData.asObservable();
   }

   populateLoggedInAssociateData(data: any){
    this.loggedInAssociateData.next(data);
   }

  registerUserDetails(registerRequestModel: any): Observable<any>{
    // console.log('Shared service printing input')
    // console.log(registerRequestModel);
    // return registerRequestModel;
    return this.http.post("http://localhost:8091/skill-tracker/api/v1/engineer/add-profile",registerRequestModel);
  }
  
  getAllSkills(): Observable<any>{
    return this.http.get("http://localhost:8091/skill-tracker/api/v1/engineer/getAllSkills");
  }

  validateUserCredentialss(associateID: any, associatePassword: any): Observable<any>{
    // console.log('Shared service printing input')
    // console.log(associateID+" "+associatePassword );
    const params = new HttpParams()
    .set('associateID',associateID)
    .set('associatePassword',associatePassword);
    return this.http.get("http://localhost:8091/skill-tracker/api/v1/engineer/validateUserCredentials",{params});
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
    return this.http.post("http://localhost:8091/skill-tracker/api/v1/engineer/update-profile",updateProfileRequestModel);

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
