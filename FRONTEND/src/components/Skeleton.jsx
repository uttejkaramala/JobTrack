export function Skeleton({ className = '' }) { return <span className={`skeleton ${className}`} />; }
export function DashboardSkeleton() {
  return <div className="dashboard-skeleton"><div className="stats-grid">{[1,2,3,4].map((x) => <div className="stat-card" key={x}><Skeleton className="sk-title" /><Skeleton className="sk-number" /><Skeleton className="sk-line" /></div>)}</div><div className="content-grid"><div className="panel skeleton-panel"><Skeleton className="sk-title" />{[1,2,3,4,5].map(x => <Skeleton className="sk-row" key={x}/>)}</div><div className="panel skeleton-panel"><Skeleton className="sk-title" />{[1,2,3].map(x => <Skeleton className="sk-row" key={x}/>)}</div></div></div>;
}
