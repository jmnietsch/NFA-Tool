import {Component, OnInit, ViewChild} from '@angular/core';
import {DataStorageService} from '../shared/data-storage.service';
import {Project} from '../shared/project.model';
import {NgForm} from '@angular/forms';

@Component({
  selector: 'app-newproject',
  templateUrl: './newproject.component.html',
  styleUrls: ['./newproject.component.css']
})
export class NewprojectComponent implements OnInit {
  messageField;
  project = this.initProject();
  constructor(private dataStorage: DataStorageService) {}
  ngOnInit() {
  }

  onSubmit() {

    this.dataStorage.storeProject(this.project).subscribe((response) => {
      console.log(response.json());
      // TODO -> check server-failure and show some message...
      this.project = response.json();
      this.messageField = 'A new project with ID ' + this.project.id + ' created';
      this.project = this.initProject();
    });
  }
  
  private initProject() {
    return new Project(null, null, null, null, null, null, null, null, null);
  }
  
  clearMessage() {
    this.messageField = '';
  }
}
