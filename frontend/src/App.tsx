import {useState} from 'react';
import Layout from './components/Layout';
import Dashboard from './pages/Dashboard';
import Students from './pages/Students';
import Employees from './pages/Employees';
import Cards from './pages/Cards';
import Devices from './pages/Devices';
import Attendance from './pages/Attendance';
export default function App(){const[p,setP]=useState('dashboard');const page=p==='dashboard'?<Dashboard/>:p==='students'?<Students/>:p==='employees'?<Employees/>:p==='cards'?<Cards/>:p==='devices'?<Devices/>:<Attendance/>;return <Layout page={p} setPage={setP}>{page}</Layout>}