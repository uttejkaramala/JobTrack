export default function Badge({ children, tone = 'neutral' }) {
  return <span className={`badge badge-${tone}`}>{children}</span>;
}

export function StatusBadge({ status }) {
  const tone = {
    SAVED: 'neutral', APPLIED: 'blue', SCREENING: 'purple', INTERVIEW: 'orange', OFFER: 'green', REJECTED: 'red', WITHDRAWN: 'gray'
  }[status] || 'neutral';
  return <Badge tone={tone}>{status.charAt(0) + status.slice(1).toLowerCase()}</Badge>;
}

export function PriorityBadge({ priority }) {
  return <Badge tone={{ HIGH: 'red', MEDIUM: 'orange', LOW: 'gray' }[priority] || 'neutral'}>{priority.charAt(0) + priority.slice(1).toLowerCase()}</Badge>;
}
