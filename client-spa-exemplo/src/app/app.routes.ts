import { Routes } from '@angular/router';
import { DefaultContent } from './components/default-content/default-content';
import { ProductPost } from './components/product-post/product-post';
import { ProductList } from './components/product-list/product-list';
import { Callback } from './components/callback/callback';

export const routes: Routes = [
    {path: "", component: DefaultContent},
    {path: "product-post", component: ProductPost},
    {path: "product-list", component: ProductList},
    {path: "callback", component: Callback}
];
