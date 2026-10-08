import { useEffect, useMemo, useState } from 'react';
import { ArrowRight, CalendarClock, CheckCircle2, Clock3 } from 'lucide-react';
import { Link } from 'react-router-dom';
import { useApp } from '../context/AppContext';
import { applicationApi } from '../services/applicationApi';

function dateOnly(d){return new Date(`${d}T00:00:00`)}
function formatDate(d){return new Date(`${d}T00:00:00`).toLocaleDateString('en-IN',{day:'2-digit',month:'short',year:'numeric'})}

export default function FollowUps(){
  const {notify}=useApp();
  const [tab,setTab]=useState('ALL');
  const [data,setData]=useState({ALL:[],OVERDUE:[],TODAY:[],UPCOMING:[]});
  const [loading,setLoading]=useState(true);

  useEffect(()=>{
    let active=true;
    Promise.all(['OVERDUE','TODAY','UPCOMING'].map(type=>applicationApi.followUps(type).then(res=>[type,res.data||[]])))
      .then(entries=>{if(!active)return;const next={ALL:[],OVERDUE:[],TODAY:[],UPCOMING:[]};for(const [type,items] of entries){next[type]=items}next.ALL=[...next.OVERDUE,...next.TODAY,...next.UPCOMING].sort((a,b)=>dateOnly(a.nextFollowUpDate)-dateOnly(b.nextFollowUpDate));setData(next)})
      .catch(err=>{if(active)notify(err.response?.data?.message||'Unable to load follow-ups.','error')})
      .finally(()=>{if(active)setLoading(false)});
    return()=>{active=false};
  },[notify]);

  const items=useMemo(()=>data[tab]||[],[data,tab]);

  return <div className="page-stack"><div className="page-heading"><div><span className="eyebrow orange">STAY ON TRACK</span><h1>Follow-ups</h1><p>Keep conversations moving without keeping everything in your head.</p></div></div>
    <div className="followup-summary"><Summary icon={Clock3} title="Overdue" value={data.OVERDUE.length} tone="red"/><Summary icon={CalendarClock} title="Today" value={data.TODAY.length} tone="orange"/><Summary icon={CheckCircle2} title="Upcoming" value={data.UPCOMING.length} tone="green"/></div>
    <div className="tabs"><button className={tab==='ALL'?'active':''} onClick={()=>setTab('ALL')}>All</button><button className={tab==='OVERDUE'?'active':''} onClick={()=>setTab('OVERDUE')}>Overdue</button><button className={tab==='TODAY'?'active':''} onClick={()=>setTab('TODAY')}>Today</button><button className={tab==='UPCOMING'?'active':''} onClick={()=>setTab('UPCOMING')}>Upcoming</button></div>
    <div className="panel followup-list">{loading?<div className="followup-skeletons">{[1,2,3].map(i=><div className="skeleton" style={{height:62,margin:8}} key={i}/>)}</div>:items.length?items.map(a=><div className="followup-row" key={a.id}><div className={`followup-status ${tab==='OVERDUE'?'overdue':''}`}><Clock3 size={18}/></div><div className="followup-copy"><strong>{a.companyName}</strong><span>{a.jobTitle}</span></div><div className="followup-date"><span>Follow up</span><strong>{formatDate(a.nextFollowUpDate)}</strong></div><Link className="text-link" to={`/applications/${a.id}`}>View application <ArrowRight size={16}/></Link></div>):<div className="empty-state compact"><div className="empty-icon"><CheckCircle2 size={26}/></div><h3>Nothing needs your attention</h3><p>You're all caught up for this view.</p></div>}</div>
  </div>;
}
function Summary({icon:Icon,title,value,tone}){return <div className="followup-card"><span className={`summary-icon ${tone}`}><Icon size={19}/></span><div><span>{title}</span><strong>{value}</strong></div></div>}
