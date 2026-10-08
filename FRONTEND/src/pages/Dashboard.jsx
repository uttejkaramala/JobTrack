import { useEffect, useState } from 'react';
import { ArrowRight, BriefcaseBusiness, CalendarDays, CheckCircle2, Clock3, Plus, TrendingUp } from 'lucide-react';
import { Link } from 'react-router-dom';
import { useApp } from '../context/AppContext';
import { dashboardApi } from '../services/dashboardApi';
import { DashboardSkeleton } from '../components/Skeleton';
import { StatusBadge } from '../components/Badge';

const pretty = (s) => s ? s.replaceAll('_',' ').toLowerCase().replace(/\b\w/g,c=>c.toUpperCase()) : '—';

export default function Dashboard(){
  const {notify}=useApp();
  const [summary,setSummary]=useState(null);
  const [loading,setLoading]=useState(true);

  useEffect(()=>{
    let active=true;
    dashboardApi.summary().then(({data})=>{if(active)setSummary(data)}).catch(err=>{if(active)notify(err.response?.data?.message||'Unable to load your dashboard.','error')}).finally(()=>{if(active)setLoading(false)});
    return()=>{active=false};
  },[notify]);

  if(loading)return <><PageHeading/><DashboardSkeleton/></>;
  if(!summary)return <div className="empty-state"><div className="empty-icon"><BriefcaseBusiness size={26}/></div><h3>We couldn't load your dashboard</h3><p>Please refresh and try again.</p></div>;

  const counts={SAVED:summary.saved||0,APPLIED:summary.applied||0,SCREENING:summary.screening||0,INTERVIEW:summary.interview||0,OFFER:summary.offers||0,REJECTED:summary.rejected||0};
  const source=(summary.applicationsBySource||[]).slice().sort((a,b)=>b.count-a.count);
  const maxSource=Math.max(...source.map(x=>x.count),1);
  const total=Math.max(summary.totalApplications||0,1);
  const upcoming=summary.upcomingInterviews||[];

  return <div className="page-stack"><PageHeading/>
    <section className="stats-grid">
      <Stat title="Applications" value={summary.totalApplications||0} note="Across all stages" icon={BriefcaseBusiness}/>
      <Stat title="Interviews" value={summary.interview||0} note="Applications in interview stage" icon={CalendarDays}/>
      <Stat title="Follow-ups" value={(summary.overdueFollowUps||0)+(summary.todayFollowUps||0)+(summary.upcomingFollowUps||0)} note={`${summary.overdueFollowUps||0} overdue · ${summary.todayFollowUps||0} today`} icon={Clock3}/>
      <Stat title="Offers" value={summary.offers||0} note="Keep the momentum" icon={CheckCircle2}/>
    </section>

    <div className="content-grid">
      <section className="panel"><div className="panel-header"><div><h2>Application pipeline</h2><p>See where your opportunities stand.</p></div><Link className="text-link" to="/applications">View all <ArrowRight size={16}/></Link></div><div className="pipeline-list">{[['SAVED','Saved'],['APPLIED','Applied'],['SCREENING','Screening'],['INTERVIEW','Interview'],['OFFER','Offer'],['REJECTED','Rejected']].map(([key,label])=><div className="pipeline-row" key={key}><span>{label}</span><div className="pipeline-track"><div style={{width:`${Math.max(counts[key]?3:0,(counts[key]/total)*100)}%`}}/></div><strong>{counts[key]}</strong></div>)}</div></section>

      <section className="panel"><div className="panel-header"><div><h2>Upcoming interviews</h2><p>Your next conversations.</p></div><Link className="text-link" to="/interviews">View all <ArrowRight size={16}/></Link></div>{upcoming.length?<div className="interview-list">{upcoming.map(i=><div className="interview-item" key={i.interviewId}><div className="date-tile"><strong>{new Date(i.interviewDate).getDate()}</strong><span>{new Date(i.interviewDate).toLocaleDateString('en-IN',{month:'short'})}</span></div><div className="interview-copy"><strong>{i.companyName}</strong><span>{i.roundName} · {new Date(i.interviewDate).toLocaleTimeString('en-IN',{hour:'numeric',minute:'2-digit'})}</span></div><StatusBadge status={i.status||'SCHEDULED'}/></div>)}</div>:<Empty text="No upcoming interviews."/>}</section>
    </div>

    <div className="content-grid lower">
      <section className="panel"><div className="panel-header"><div><h2>Applications by source</h2><p>Where your opportunities come from.</p></div></div><div className="source-list">{source.slice(0,5).map(item=><div className="source-row" key={item.source}><span>{pretty(item.source)}</span><div className="source-track"><div style={{width:`${(item.count/maxSource)*100}%`}}/></div><strong>{item.count}</strong></div>)}</div>{!source.length&&<Empty text="No application source data yet."/>}</section>
      <section className="panel attention-panel"><div className="panel-header"><div><h2>Keep moving</h2><p>A few quick actions for today.</p></div><TrendingUp size={21}/></div><Link className="quick-action" to="/applications?new=true"><span className="quick-icon"><Plus size={18}/></span><span><strong>Add a new application</strong><small>Keep your pipeline up to date.</small></span><ArrowRight size={17}/></Link><Link className="quick-action" to="/follow-ups"><span className="quick-icon"><Clock3 size={18}/></span><span><strong>Review follow-ups</strong><small>Don't let good opportunities go quiet.</small></span><ArrowRight size={17}/></Link></section>
    </div>
  </div>;
}
function PageHeading(){const user=JSON.parse(localStorage.getItem('jobtrack_user')||'{}');const firstName=(user.name||'there').split(' ')[0];return <div className="page-heading"><div><span className="eyebrow orange">OVERVIEW</span><h1>Good morning, {firstName}.</h1><p>Here's a clear view of your job search.</p></div><Link to="/applications?new=true" className="primary-button"><Plus size={18}/> Add application</Link></div>}
function Stat({title,value,note,icon:Icon}){return <div className="stat-card"><div className="stat-icon"><Icon size={19}/></div><div className="stat-label">{title}</div><strong className="stat-value">{value}</strong><span className="stat-note">{note}</span></div>}
function Empty({text}){return <div className="empty-inline"><CalendarDays size={20}/><span>{text}</span></div>}
