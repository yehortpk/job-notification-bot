import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class PaginationService {
  generatePagination(currentPage: number, totalPages: number): string[] {
    if (totalPages <= 5) {
      return Array.from({ length: 5 }, (_, i) => (i + 1).toString())
    }

    const result = [(currentPage - 1).toString(), currentPage.toString(), (currentPage + 1).toString()];
    if (currentPage <= 1) {
      result.shift();
    }
    if (currentPage >= totalPages - 1) {
      result.pop();
    }

    if ((currentPage - 2) > 1) {
      result.unshift("1", "...");
    } else if ((currentPage - 1) > 1){
      result.unshift("1");
    }

    if ((currentPage + 2) < totalPages) {
      result.push("...", totalPages.toString());
    } else if ((currentPage + 1) < totalPages) {
      result.push(totalPages.toString());
    }

    return result;
  }
}
