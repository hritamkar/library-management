using my.library as my from '../db/data-model';

service LibraryService @(path:'/LibraryService'){
   entity Books as projection on my.Book;
   entity Members as projection on my.Member;
   entity Loans as projection on my.Loan;

   type ReturnBookResponse {
      message : String;
      fine    : Integer;
   }

   action returnBook(loanId : UUID, email : String) returns ReturnBookResponse;
}

// annotate LibraryService.Books with @restrict: [
//   { grant: 'READ', to: 'any' },
//   { grant: 'CREATE', to: 'Admin' }
// ];
