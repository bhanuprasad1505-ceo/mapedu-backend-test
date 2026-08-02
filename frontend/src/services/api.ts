const API_BASE = import.meta.env.VITE_API_BASE_URL || '/api';
async function request<T>(path:string, options:RequestInit={}):Promise<T>{const r=await fetch(`${API_BASE}${path}`,{...options,headers:{'Content-Type':'application/json',...(options.headers||{})}});const text=await r.text();let data:any=null;try{data=text?JSON.parse(text):null}catch{data=text}if(!r.ok)throw new Error(data?.message||`HTTP ${r.status}`);return data as T;}
export const api={
 health:()=>request<any>('/health'),
 dashboard:(s:string)=>request<any>(`/dashboard?schoolCode=${encodeURIComponent(s)}`),
 students:(s:string)=>request<any[]>(`/students?schoolCode=${encodeURIComponent(s)}`),
 employees:(s:string)=>request<any[]>(`/employees?schoolCode=${encodeURIComponent(s)}`),
 cards:(s:string)=>request<any[]>(`/rfid/cards?schoolCode=${encodeURIComponent(s)}`),
 devices:(s:string)=>request<any[]>(`/devices?schoolCode=${encodeURIComponent(s)}`),
 attendance:(s:string)=>request<any[]>(`/attendance?schoolCode=${encodeURIComponent(s)}`),
 createStudent:(x:any)=>request('/students',{method:'POST',body:JSON.stringify(x)}),
 createEmployee:(x:any)=>request('/employees',{method:'POST',body:JSON.stringify(x)}),
 createCard:(x:any)=>request('/rfid/cards',{method:'POST',body:JSON.stringify(x)}),
 createDevice:(x:any)=>request('/devices',{method:'POST',body:JSON.stringify(x)})
};