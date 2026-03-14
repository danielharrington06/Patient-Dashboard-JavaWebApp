function setupFormValidation(formId) {
    const form = document.getElementById(formId);

    document.getElementById('field-SSN').addEventListener('input', function () {
        let v = this.value.replace(/\D/g, '');
        if (v.length > 5)      v = v.slice(0,3) + '-' + v.slice(3,5) + '-' + v.slice(5,9);
        else if (v.length > 3) v = v.slice(0,3) + '-' + v.slice(3);
        this.value = v;
    });

    function validateDates() {
        const birth = document.getElementById('field-BIRTHDATE').value;
        const death = document.getElementById('field-DEATHDATE').value;
        const errDeath = document.getElementById('err-DEATHDATE');
        if (birth && death && death < birth) {
            errDeath.textContent = 'Date of death cannot be before date of birth.';
            document.getElementById('field-DEATHDATE').setCustomValidity('invalid');
            return false;
        }
        errDeath.textContent = '';
        document.getElementById('field-DEATHDATE').setCustomValidity('');
        return true;
    }

    document.getElementById('field-BIRTHDATE').addEventListener('change', validateDates);
    document.getElementById('field-DEATHDATE').addEventListener('change', validateDates);

    form.addEventListener('submit', function (e) {
        let valid = true;

        const existingBanner = document.getElementById('validation-banner');
        if (existingBanner) existingBanner.remove();

        [
            { id: 'field-FIRST',     errId: 'err-FIRST',     msg: 'First name is required.' },
            { id: 'field-LAST',      errId: 'err-LAST',      msg: 'Last name is required.' },
            { id: 'field-BIRTHDATE', errId: 'err-BIRTHDATE', msg: 'Date of birth is required.' },
            { id: 'field-GENDER',    errId: 'err-GENDER',    msg: 'Gender is required.' },
            { id: 'field-SSN',       errId: 'err-SSN',       msg: 'SSN is required.' }
        ].forEach(({ id, errId, msg }) => {
            const el  = document.getElementById(id);
            const err = document.getElementById(errId);
            if (!el.value.trim()) {
                if (err) err.textContent = msg;
                el.classList.add('input-error');
                valid = false;
            } else {
                if (err) err.textContent = '';
                el.classList.remove('input-error');
            }
        });

        const today = new Date().toISOString().split('T')[0];

        const birthEl = document.getElementById('field-BIRTHDATE');
        const birthErr = document.getElementById('err-BIRTHDATE');
        if (birthEl.value) {
            if (birthEl.value > today) {
                birthErr.textContent = 'Date of birth cannot be in the future.';
                birthEl.classList.add('input-error');
                valid = false;
            } else if (birthEl.value < '1900-01-01') {
                birthErr.textContent = 'Date of birth must be on or after 01/01/1900.';
                birthEl.classList.add('input-error');
                valid = false;
            }
        }

        const deathEl = document.getElementById('field-DEATHDATE');
        const deathErr = document.getElementById('err-DEATHDATE');
        if (deathEl.value && deathEl.value > today) {
            deathErr.textContent = 'Date of death cannot be in the future.';
            deathEl.classList.add('input-error');
            valid = false;
        }

        const ssn = document.getElementById('field-SSN');
        const ssnErr = document.getElementById('err-SSN');
        if (ssn.value && !/^\d{3}-\d{2}-\d{4}$/.test(ssn.value)) {
            ssnErr.textContent = 'Format: 999-12-3456';
            ssn.classList.add('input-error');
            valid = false;
        }

        const zip = document.getElementById('field-ZIP');
        const zipErr = document.getElementById('err-ZIP');
        if (zip && zip.value && !/^\d{5}$/.test(zip.value)) {
            zipErr.textContent = 'ZIP must be exactly 5 digits.';
            zip.classList.add('input-error');
            valid = false;
        }

        if (!validateDates()) valid = false;

        if (!valid) {
            e.preventDefault();
            const banner = document.createElement('div');
            banner.id = 'validation-banner';
            banner.className = 'error';
            banner.style.marginBottom = 'var(--spacing-md)';
            banner.textContent = 'One or more fields have errors — please review and correct them before saving.';
            form.insertBefore(banner, form.firstChild);
            window.scrollTo({ top: 0, behavior: 'smooth' });
        }
    });

    form.querySelectorAll('input, select').forEach(el => {
        el.addEventListener('input', () => {
            el.classList.remove('input-error');
            const errId = el.id.replace('field-', 'err-');
            const err = document.getElementById(errId);
            if (err) err.textContent = '';
        });
    });
}