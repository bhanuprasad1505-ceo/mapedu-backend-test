const API_BASE = (import.meta.env.VITE_API_BASE_URL || '/api').replace(/\/$/, '');
async function request<T>(path:string, options:RequestInit={}):Promise<T>{const r=await fetch(`${API_BASE}${path}`,{...options,headers:{'Content-Type':'application/json',...(options.headers||{})}});const text=await r.text();let data:any=null;try{data=text?JSON.parse(text):null}catch{data=text}if(!r.ok)throw new Error(data?.message||data?.error||`HTTP ${r.status}`);return data as T;}
export const api={
 health:()=>request<any>('/health'),
 dashboard:(s:string)=>request<any>(`/dashboard?schoolCode=${encodeURIComponent(s)}`),
 students:(s:string)=>request<any[]>(`/students?schoolCode=${encodeURIComponent(s)}`),
 student:(id:string)=>request<any>(`/students/${encodeURIComponent(id)}`),
 createStudent:(x:any)=>request<any>('/students',{method:'POST',body:JSON.stringify(x)}),
 employees:(s:string)=>request<any[]>(`/employees?schoolCode=${encodeURIComponent(s)}`),
 employee:(id:string)=>request<any>(`/employees/${encodeURIComponent(id)}`),
 createEmployee:(x:any)=>request<any>('/employees',{method:'POST',body:JSON.stringify(x)}),
 cards:(s:string)=>request<any[]>(`/rfid/cards?schoolCode=${encodeURIComponent(s)}`),
 card:(uid:string)=>request<any>(`/rfid/cards/${encodeURIComponent(uid)}`),
 createCard:(x:any)=>request<any>('/rfid/cards',{method:'POST',body:JSON.stringify(x)}),
 deactivateCard:(uid:string)=>request<void>(`/rfid/cards/${encodeURIComponent(uid)}`,{method:'DELETE'}),
 devices:(s:string)=>request<any[]>(`/devices?schoolCode=${encodeURIComponent(s)}`),
 device:(id:string)=>request<any>(`/devices/${encodeURIComponent(id)}`),
 createDevice:(x:any)=>request<any>('/devices',{method:'POST',body:JSON.stringify(x)}),
 heartbeat:(id:string,firmwareVersion?:string)=>request<any>(`/devices/${encodeURIComponent(id)}/heartbeat`,{method:'POST',body:JSON.stringify({firmwareVersion})}),
 attendance:(s:string)=>request<any[]>(`/attendance?schoolCode=${encodeURIComponent(s)}`),
 attendanceByDevice:(id:string)=>request<any[]>(`/attendance/device/${encodeURIComponent(id)}`),
 markAttendance:(x:any)=>request<any>('/attendance',{method:'POST',body:JSON.stringify(x)})
};