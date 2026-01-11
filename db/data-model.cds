namespace my.library;

using {Currency, sap, managed, cuid} from '@sap/cds/common';

entity Book : cuid, managed {
    title  : localized String;
    author : String;
    stock  : Integer;
}

entity Member : cuid, managed{
    name  : String;
    email : String;
}

entity Loan : cuid, managed {
    book       : Association to Book;
    member     : Association to Member;
    loanDate   : Date;
    dueDate    : Date;
    returnDate : Date;
    fineAmount : Integer;
}