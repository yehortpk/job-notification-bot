import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { MatStepperModule } from '@angular/material/stepper';
import { MatIconModule } from '@angular/material/icon';
import { ParsingService } from '../service/parsing.service';
import { ParsingProgress } from './progress-bar.model';

@Component({
  selector: 'app-parser-progress',
  standalone: true,
  imports: [CommonModule, MatStepperModule, MatIconModule],
  templateUrl: './parser-progress.component.html',
  styleUrl: './parser-progress.component.css',
  host: {
    '[class.custom-stepper]': 'true'
  }
})
export class ParserProgressComponent implements OnInit {
  protected isParsingStartAllowed: boolean = true;
  protected progress: ParsingProgress = {
    parsers: [],
    finished: false,
    new: 0,
    outdated: 0,
    total: 0
  };
  private progressInterval: any;
  constructor(private parsingService: ParsingService){};

  ngOnInit(): void {
    this.parsingService.getProgress().subscribe((pr) => this.progress = pr);
  }

  ngOnDestroy() {
    clearInterval(this.progressInterval);
  }

  startParsing() {
    this.parsingService.startParsing();
    this.isParsingStartAllowed = false;
    
    this.progressInterval = setInterval(() => {
        this.parsingService.getProgress().subscribe(
        (pr) => {
          this.progress = pr;
          if (pr.finished) {
            clearInterval(this.progressInterval);
            this.isParsingStartAllowed = true;
          }
        }
        );
    }, 1000);
  }
}
