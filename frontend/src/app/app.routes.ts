import { Routes } from '@angular/router';


import { Inicio } from './paginas/inicio/inicio';
import { Tienda } from './paginas/tienda/tienda';
import { Carrito } from './paginas/carrito/carrito';
import { Compras } from './paginas/compras/compras';
import { Turnero } from './paginas/turnero/turnero';
import { Login } from './paginas/mi-cuenta/login/login';
import { Registro } from './paginas/mi-cuenta/registro/registro';
import { Panel } from './paginas/mi-cuenta/panel/panel';

export const routes: Routes = [

    { path: 'inicio', component: Inicio },
    { path: 'tienda', component: Tienda },
    { path: 'carrito', component: Carrito },
    { path: 'compras', component: Compras },
    { path: 'turnero', component: Turnero },


    {
        path: 'mi-cuenta',
        children: [
            { path: 'login', component: Login },
            { path: 'registro', component: Registro },
            { path: 'panel', component: Panel },
            { path: '', redirectTo: 'panel', pathMatch: 'full' }
        ]
    },


    { path: '', redirectTo: '/inicio', pathMatch: 'full' },
    { path: '**', redirectTo: '/inicio' }
];
