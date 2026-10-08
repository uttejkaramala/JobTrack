import { useEffect, useMemo, useState } from 'react';
import { BriefcaseBusiness, Edit3, Filter, Plus, Search, Trash2, X } from 'lucide-react';
import { Link, useSearchParams } from 'react-router-dom';
import { useApp } from '../context/AppContext';
import { applicationApi } from '../services/applicationApi';
import { PriorityBadge, StatusBadge } from '../components/Badge';
import Modal from '../components/Modal';
import { Skeleton } from '../components/Skeleton';

const initial = {
  companyName: '', jobTitle: '', location: '', workMode: 'REMOTE', employmentType: 'FULL_TIME',
  source: 'LINKEDIN', priority: 'MEDIUM', appliedDate: new Date().toISOString().slice(0, 10),
  nextFollowUpDate: '', salaryMin: '', salaryMax: '', jobUrl: '', status: 'SAVED'
};

const statuses = ['SAVED','APPLIED','SCREENING','INTERVIEW','OFFER','REJECTED','WITHDRAWN'];
const priorities = ['HIGH','MEDIUM','LOW'];
const sources = ['LINKEDIN','NAUKRI','INDEED','WELLFOUND','COMPANY_WEBSITE','REFERRAL','OTHER'];
const pretty = s => s?.replaceAll('_',' ').toLowerCase().replace(/\b\w/g,c=>c.toUpperCase());

export default function Applications() {
  const { notify } = useApp();
  const [params, setParams] = useSearchParams();
  const [applications, setApplications] = useState([]);
  const [pageData, setPageData] = useState({ number: 0, totalPages: 0, totalElements: 0, size: 10 });
  const [query, setQuery] = useState('');
  const [status, setStatus] = useState('');
  const [priority, setPriority] = useState('');
  const [source, setSource] = useState('');
  const [showFilters, setShowFilters] = useState(false);
  const [modal, setModal] = useState(params.get('new') === 'true' || Boolean(params.get('edit')));
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(initial);
  const [view, setView] = useState('table');
  const [confirm, setConfirm] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const requestParams = useMemo(() => ({
    page: Number(params.get('page') || 0),
    size: view === 'pipeline' ? 100 : 10,
    sort: 'createdAt,desc',
    ...(query.trim() ? { search: query.trim() } : {}),
    ...(status ? { status } : {}),
    ...(priority ? { priority } : {}),
    ...(source ? { source } : {}),
  }), [params, query, status, priority, source, view]);

  useEffect(() => {
    let active = true;
    const timer = window.setTimeout(async () => {
      setLoading(true);
      setError('');
      try {
        const { data } = await applicationApi.list(requestParams);
        if (!active) return;
        setApplications(data.content || []);
        setPageData({
          number: data.number ?? requestParams.page,
          totalPages: data.totalPages ?? 0,
          totalElements: data.totalElements ?? 0,
          size: data.size ?? requestParams.size,
        });
      } catch (err) {
        if (!active) return;
        setError(err.response?.data?.message || 'Unable to load applications.');
      } finally {
        if (active) setLoading(false);
      }
    }, 250);
    return () => { active = false; window.clearTimeout(timer); };
  }, [requestParams]);

  useEffect(() => {
    const editId = params.get('edit');
    if (!editId) return;
    let active = true;
    applicationApi.getById(editId).then(({ data }) => {
      if (!active) return;
      setEditing(data);
      setForm(toForm(data));
      setModal(true);
      params.delete('edit');
      setParams(params, { replace: true });
    }).catch(err => {
      if (active) notify(err.response?.data?.message || 'Unable to open the application for editing.', 'error');
    });
    return () => { active = false; };
  }, [params, setParams, notify]);

  const openNew = () => {
    setEditing(null);
    setForm(initial);
    setModal(true);
    params.delete('new');
    params.delete('edit');
    setParams(params, { replace: true });
  };

  const openEdit = (application) => {
    setEditing(application);
    setForm(toForm(application));
    setModal(true);
  };

  const submit = async (e) => {
    e.preventDefault();
    const payload = normalizePayload(form);
    try {
      if (editing) {
        const { data } = await applicationApi.update(editing.id, payload);
        setApplications(current => current.map(item => item.id === editing.id ? data : item));
        notify('Application updated successfully.');
      } else {
        const { data } = await applicationApi.create(payload);
        setApplications(current => [data, ...current]);
        setPageData(current => ({ ...current, totalElements: current.totalElements + 1 }));
        notify('Application added successfully.');
      }
      setModal(false);
    } catch (err) {
      notify(err.response?.data?.message || 'Unable to save the application.', 'error');
    }
  };

  const remove = async () => {
    if (!confirm) return;
    try {
      await applicationApi.remove(confirm.id);
      setApplications(current => current.filter(item => item.id !== confirm.id));
      setPageData(current => ({ ...current, totalElements: Math.max(0, current.totalElements - 1) }));
      notify('Application deleted.');
    } catch (err) {
      notify(err.response?.data?.message || 'Unable to delete the application.', 'error');
    } finally {
      setConfirm(null);
    }
  };

  const goToPage = (page) => {
    if (page < 0 || page >= pageData.totalPages) return;
    params.set('page', String(page));
    setParams(params);
  };

  const clearFilters = () => {
    setStatus(''); setPriority(''); setSource(''); setQuery('');
    params.delete('page');
    setParams(params);
  };

  const activeFilterCount = [status, priority, source].filter(Boolean).length;

  return (
    <div className="page-stack">
      <div className="page-heading">
        <div><span className="eyebrow orange">WORKSPACE</span><h1>Applications</h1><p>Keep every opportunity easy to find and easy to act on.</p></div>
        <button className="primary-button" onClick={openNew}><Plus size={18}/> Add application</button>
      </div>

      <div className="toolbar">
        <div className="search-box"><Search size={18}/><input value={query} onChange={e=>{setQuery(e.target.value); params.delete('page'); setParams(params);}} placeholder="Search company, role or location…"/></div>
        <button className={`secondary-button ${showFilters?'selected':''}`} onClick={()=>setShowFilters(!showFilters)}><Filter size={17}/> Filters{activeFilterCount>0&&<span className="filter-count">{activeFilterCount}</span>}</button>
        <div className="view-toggle"><button className={view==='table'?'selected':''} onClick={()=>setView('table')}>Table</button><button className={view==='pipeline'?'selected':''} onClick={()=>setView('pipeline')}>Pipeline</button></div>
      </div>

      {showFilters && <div className="filter-panel">
        <FilterSelect label="Status" value={status} setValue={value=>{setStatus(value); params.delete('page'); setParams(params)}} options={statuses}/>
        <FilterSelect label="Priority" value={priority} setValue={value=>{setPriority(value); params.delete('page'); setParams(params)}} options={priorities}/>
        <FilterSelect label="Source" value={source} setValue={value=>{setSource(value); params.delete('page'); setParams(params)}} options={sources}/>
        <button className="clear-filter" onClick={clearFilters}><X size={15}/> Clear</button>
      </div>}

      {loading ? <ApplicationsSkeleton /> : error ? (
        <div className="empty-state"><div className="empty-icon"><Search size={26}/></div><h3>Unable to load applications</h3><p>{error}</p><button className="primary-button" onClick={()=>setParams(new URLSearchParams(params))}>Try again</button></div>
      ) : view === 'table' ? (
        <div className="panel table-panel">
          {applications.length ? <div className="table-wrap"><table><thead><tr><th>Company</th><th>Role</th><th>Status</th><th>Priority</th><th>Applied</th><th>Follow-up</th><th></th></tr></thead><tbody>
            {applications.map(a=><tr key={a.id}>
              <td><Link to={`/applications/${a.id}`} className="company-link"><span className="company-avatar">{a.companyName?.charAt(0)?.toUpperCase()}</span><span><strong>{a.companyName}</strong><small>{a.location||'Location not added'}</small></span></Link></td>
              <td>{a.jobTitle}</td><td><StatusBadge status={a.status}/></td><td><PriorityBadge priority={a.priority}/></td>
              <td>{formatDate(a.appliedDate)}</td><td>{a.nextFollowUpDate?formatDate(a.nextFollowUpDate):'—'}</td>
              <td><div className="row-actions"><button className="icon-button" onClick={()=>openEdit(a)} aria-label="Edit"><Edit3 size={17}/></button><button className="icon-button danger-icon" onClick={()=>setConfirm(a)} aria-label="Delete"><Trash2 size={17}/></button></div></td>
            </tr>)}
          </tbody></table></div> : <EmptyState hasFilters={Boolean(query || activeFilterCount)} onAdd={openNew} onClear={clearFilters}/>} 
          {applications.length > 0 && <div className="table-footer"><span>Showing {applications.length} of {pageData.totalElements} applications</span><div><button className="pagination-button" disabled={pageData.number===0} onClick={()=>goToPage(pageData.number-1)}>Previous</button><button className="pagination-button selected">{pageData.number+1}</button><button className="pagination-button" disabled={pageData.number+1>=pageData.totalPages} onClick={()=>goToPage(pageData.number+1)}>Next</button></div></div>}
        </div>
      ) : (
        <Pipeline applications={applications}/>
      )}

      <Modal open={modal} title={editing?'Edit application':'Add application'} onClose={()=>setModal(false)}><ApplicationForm form={form} setForm={setForm} submit={submit} editing={!!editing} close={()=>setModal(false)}/></Modal>
      <Modal open={!!confirm} title="Delete application?" onClose={()=>setConfirm(null)} width="430px"><div className="confirm-content"><div className="confirm-icon"><Trash2 size={21}/></div><p>This will remove <strong>{confirm?.companyName}</strong> and its related interviews and notes. This action cannot be undone.</p><div className="modal-actions"><button className="secondary-button" onClick={()=>setConfirm(null)}>Cancel</button><button className="danger-button" onClick={remove}>Delete application</button></div></div></Modal>
    </div>
  );
}

function ApplicationsSkeleton(){
  return <div className="panel table-panel"><div className="table-wrap"><table><thead><tr><th>Company</th><th>Role</th><th>Status</th><th>Priority</th><th>Applied</th><th>Follow-up</th><th></th></tr></thead><tbody>{Array.from({length:6}).map((_,i)=><tr key={i}><td><Skeleton className="table-skeleton"/></td><td><Skeleton className="table-skeleton short"/></td><td><Skeleton className="table-skeleton badge"/></td><td><Skeleton className="table-skeleton priority"/></td><td><Skeleton className="table-skeleton date"/></td><td><Skeleton className="table-skeleton date"/></td><td><Skeleton className="table-skeleton action"/></td></tr>)}</tbody></table></div></div>;
}

function ApplicationForm({form,setForm,submit,editing,close}){
  const set=(key,value)=>setForm({...form,[key]:value});
  return <form className="app-form" onSubmit={submit}>
    <div className="form-section"><h3>Basic information</h3><div className="form-grid">
      <Field label="Company name *"><input value={form.companyName} onChange={e=>set('companyName',e.target.value)} required placeholder="e.g. Google"/></Field>
      <Field label="Job title *"><input value={form.jobTitle} onChange={e=>set('jobTitle',e.target.value)} required placeholder="e.g. Software Engineer"/></Field>
      <Field label="Location"><input value={form.location} onChange={e=>set('location',e.target.value)} placeholder="e.g. Bangalore"/></Field>
      <Field label="Job URL"><input type="url" value={form.jobUrl} onChange={e=>set('jobUrl',e.target.value)} placeholder="https://…"/></Field>
    </div></div>
    <div className="form-section"><h3>Job details</h3><div className="form-grid">
      <Select label="Work mode" value={form.workMode} set={v=>set('workMode',v)} options={['REMOTE','HYBRID','ONSITE']}/>
      <Select label="Employment type" value={form.employmentType} set={v=>set('employmentType',v)} options={['FULL_TIME','INTERNSHIP','CONTRACT','PART_TIME']}/>
      <Select label="Source" value={form.source} set={v=>set('source',v)} options={sources}/>
      <Select label="Priority" value={form.priority} set={v=>set('priority',v)} options={priorities}/>
      <Field label="Salary min"><input type="number" min="0" value={form.salaryMin} onChange={e=>set('salaryMin',e.target.value)} placeholder="₹"/></Field>
      <Field label="Salary max"><input type="number" min="0" value={form.salaryMax} onChange={e=>set('salaryMax',e.target.value)} placeholder="₹"/></Field>
    </div></div>
    <div className="form-section"><h3>Application</h3><div className="form-grid">
      <Select label="Status" value={form.status} set={v=>set('status',v)} options={statuses}/>
      <Field label="Applied date"><input type="date" value={form.appliedDate||''} onChange={e=>set('appliedDate',e.target.value)}/></Field>
      <Field label="Next follow-up"><input type="date" value={form.nextFollowUpDate||''} onChange={e=>set('nextFollowUpDate',e.target.value)}/></Field>
    </div></div>
    <div className="modal-actions"><button type="button" className="secondary-button" onClick={close}>Cancel</button><button className="primary-button">{editing?'Save changes':'Add application'}</button></div>
  </form>;
}
function Field({label,children}){return <label className="field">{label}{children}</label>}
function Select({label,value,set,options}){return <Field label={label}><select value={value||''} onChange={e=>set(e.target.value)}>{options.map(o=><option key={o} value={o}>{pretty(o)}</option>)}</select></Field>}
function FilterSelect({label,value,setValue,options}){return <label className="filter-select"><span>{label}</span><select value={value} onChange={e=>setValue(e.target.value)}><option value="">All</option>{options.map(o=><option key={o} value={o}>{pretty(o)}</option>)}</select></label>}
function Pipeline({applications}){const stages=['SAVED','APPLIED','SCREENING','INTERVIEW','OFFER'];return <div className="pipeline-board">{stages.map(stage=><div className="pipeline-column" key={stage}><div className="pipeline-column-title"><span>{pretty(stage)}</span><strong>{applications.filter(a=>a.status===stage).length}</strong></div>{applications.filter(a=>a.status===stage).map(a=><Link className="pipeline-card" key={a.id} to={`/applications/${a.id}`}><span className="company-avatar">{a.companyName?.charAt(0)?.toUpperCase()}</span><strong>{a.companyName}</strong><span>{a.jobTitle}</span><StatusBadge status={a.status}/></Link>)}</div>)}</div>}
function EmptyState({hasFilters,onAdd,onClear}){return <div className="empty-state"><div className="empty-icon"><BriefcaseBusiness size={26}/></div><h3>{hasFilters?'No applications found':'No applications yet'}</h3><p>{hasFilters?'Try changing or clearing your search and filters.':'Start tracking your job search by adding your first application.'}</p><div className="empty-actions">{hasFilters&&<button className="secondary-button" onClick={onClear}><X size={16}/> Clear search</button>}<button className="primary-button" onClick={onAdd}><Plus size={17}/> Add application</button></div></div>}
function toForm(a){return {...initial,...a,salaryMin:a.salaryMin??'',salaryMax:a.salaryMax??'',nextFollowUpDate:a.nextFollowUpDate||''};}
function normalizePayload(form){return {...form,salaryMin:form.salaryMin===''?null:Number(form.salaryMin),salaryMax:form.salaryMax===''?null:Number(form.salaryMax),nextFollowUpDate:form.nextFollowUpDate||null,appliedDate:form.appliedDate||null};}
function formatDate(d){return d?new Date(`${d}T00:00:00`).toLocaleDateString('en-IN',{day:'2-digit',month:'short',year:'numeric'}):'—'}
