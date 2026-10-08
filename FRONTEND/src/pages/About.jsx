import { ArrowRight, BriefcaseBusiness, CheckCircle2, Code2, Database, Layers3, ShieldCheck } from 'lucide-react';
import { Link } from 'react-router-dom';

const technologies = [
  { icon: Code2, title: 'Backend', items: ['Java 17', 'Spring Boot', 'Spring Security', 'JWT', 'Spring Data JPA'] },
  { icon: Layers3, title: 'Frontend', items: ['React', 'JavaScript', 'Axios', 'React Router'] },
  { icon: Database, title: 'Data & API', items: ['PostgreSQL', 'REST APIs', 'OpenAPI / Swagger', 'Validation & exception handling'] },
];

const highlights = [
  'JWT authentication and secure password hashing',
  'User-specific data ownership and protected APIs',
  'DTO-based REST API design with validation',
  'Application search, filtering and pagination',
  'Interview and follow-up management',
  'Dashboard analytics and application pipeline',
];

export default function About() {
  return (
    <div className="page-stack about-page">
      <section className="about-hero panel">
        <span className="eyebrow orange">ABOUT JOBTRACK</span>
        <h1>Built to make the job search easier to manage.</h1>
        <p>
          JobTrack is a full-stack application designed to keep job applications,
          interviews, notes and follow-ups organized in one focused workspace.
        </p>
        <div className="about-hero-actions">
          <Link className="primary-button" to="/applications">Explore applications <ArrowRight size={17} /></Link>
          <span className="about-built-by">Designed &amp; built by <strong>Uttej Karamala</strong></span>
        </div>
      </section>

      <section className="about-grid">
        <div className="panel about-story">
          <div className="about-section-heading">
            <span className="about-icon"><BriefcaseBusiness size={20} /></span>
            <div><h2>About the creator</h2><p>A practical project built around real full-stack development.</p></div>
          </div>
          <p>
            I'm <strong>Uttej Karamala</strong>, a Computer Science &amp; Engineering graduate
            specializing in Artificial Intelligence &amp; Machine Learning. I enjoy building
            practical applications with Java, Spring Boot and React.
          </p>
          <p>
            I built JobTrack to turn a familiar problem into a complete product: keeping track
            of where every job application stands, what needs attention next, and what happened
            during the interview process.
          </p>
        </div>

        <div className="panel about-purpose">
          <span className="eyebrow orange">WHY IT EXISTS</span>
          <h2>One clear place for every opportunity.</h2>
          <p>Instead of spreading applications across notes, spreadsheets and browser tabs, JobTrack keeps the important information together.</p>
          <div className="about-check-list">
            {['Track application progress', 'Prepare for interviews', 'Remember recruiter details', 'Stay on top of follow-ups'].map(item => (
              <div key={item}><CheckCircle2 size={18} /> <span>{item}</span></div>
            ))}
          </div>
        </div>
      </section>

      <section className="panel about-section">
        <div className="about-section-heading standalone">
          <span className="about-icon"><Code2 size={20} /></span>
          <div><h2>Built with</h2><p>The technologies and engineering practices behind JobTrack.</p></div>
        </div>
        <div className="technology-grid">
          {technologies.map(({ icon: Icon, title, items }) => (
            <article className="technology-card" key={title}>
              <Icon size={21} />
              <h3>{title}</h3>
              <div className="technology-list">{items.map(item => <span key={item}>{item}</span>)}</div>
            </article>
          ))}
        </div>
      </section>

      <section className="panel about-section">
        <div className="about-section-heading standalone">
          <span className="about-icon"><ShieldCheck size={20} /></span>
          <div><h2>Project highlights</h2><p>JobTrack goes beyond a basic CRUD application.</p></div>
        </div>
        <div className="highlight-grid">
          {highlights.map(item => <div className="highlight-item" key={item}><CheckCircle2 size={18} /><span>{item}</span></div>)}
        </div>
      </section>

      <footer className="about-footer">
        <strong>JobTrack</strong>
        <span>Designed &amp; built by Uttej Karamala · 2026</span>
      </footer>
    </div>
  );
}
