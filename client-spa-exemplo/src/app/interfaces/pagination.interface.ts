import { IPage } from "./page.interface";

export interface IPagination<T> {
    content: T[];
    page: IPage;
}